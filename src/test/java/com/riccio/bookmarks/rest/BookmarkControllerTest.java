package com.riccio.bookmarks.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riccio.bookmarks.model.Bookmark;
import com.riccio.bookmarks.service.BookmarkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN","USER"})
public class BookmarkControllerTest {
    @Autowired
    MockMvc mvc;
    @SpyBean
    BookmarkService bookmarkService;
    @Autowired  @Qualifier("halObjectMapper")
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        Mockito.reset(bookmarkService);
    }

    @Test
    public void getABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        assertNotNull(output.getContent().getUrl());
        assertEquals(input.getUrl(), output.getContent().getUrl());
    }

    @Test
    public void deleteABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                .with(csrf())
        ).andDo(print()).andExpect(status().isGone());

        mvc.perform(
                get(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
        ).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void deleteABookmarkTwiceYieldsNotModified() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .with(csrf())
        ).andDo(print()).andExpect(status().isGone());

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .with(csrf())
        ).andDo(print()).andExpect(status().isNotModified());
    }

    @Test
    public void updateABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);

        String result = mvc.perform(
                post(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://commonweb.org")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        output = objectMapper.readValue(result, new TypeReference<Resource<Bookmark>>() {
        });

        assertEquals("http://commonweb.org", output.getContent().getUrl());
    }

    @Test
    public void updateABookmarkFailsBecauseDescriptionIsNull() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        output.getContent().setDescription(null);
        mvc.perform(
                post(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent()))
                        .with(csrf())
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("description"));
        Mockito.verify(bookmarkService, never()).update(Mockito.any(Bookmark.class));

    }

    @Test
    public void updateABookmarkFailsBecauseUrlIsNotValid() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        output.getContent().setUrl("broken://url.me");
        mvc.perform(
                post(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent()))
                        .with(csrf())
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("url"));
        Mockito.verify(bookmarkService, never()).update(Mockito.any(Bookmark.class));

    }

    @Test
    public void updateABookmarkStaleFails() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        mvc.perform(
                post(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://somethingweb.com")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(
                post(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://somethingweb.com2")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isConflict());
    }

    @Test
    public void updateABookmarkFailWrongId() throws Exception {
        Bookmark input = getSimpleBookmark();

        mvc.perform(
                post("/bookmark/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(input))
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Bookmark getSimpleBookmark() {
        return new Bookmark("nice publisher", "http://riccio.com");
    }

    private Resource<Bookmark> getBookmark(String location) throws Exception {
        String result = mvc.perform(
                get(location)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
        ).andDo(print())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(result, new TypeReference<Resource<Bookmark>>() {
        });
    }

    private String addBookmark(Bookmark input) throws Exception {
        return mvc.perform(
                post("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(input))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}
