package com.java.xval.val;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class PmsSkuStockControllerIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    public void pmsSkuStock() throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        String pmsSkuStock = "{\"price\": \"-12\", \"skuCode\" : \"bob\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/val/pmsSkuStock/add")
                .content(pmsSkuStock)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(textPlainUtf8));
    }
}
