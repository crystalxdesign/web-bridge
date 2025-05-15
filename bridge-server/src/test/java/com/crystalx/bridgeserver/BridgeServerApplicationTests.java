package com.crystalx.bridgeserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BridgeServerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	//@Test
	public void addOpeningHours() throws Exception {

		this.mockMvc.perform(post("/api/v1/openingHours")
                .param("ccid", "1")
				.param("name", "new opening hours")
				.param("dayscope", "1")
        		.param("start", "2021-04-08T11:00:00.000Z")
				.param("end", "2021-04-08T12:00:00.000Z"))
				.andDo(print()).andExpect(status().isOk());
	}

	//@Test
	public void editOpeningHours() throws Exception {

		this.mockMvc.perform(put("/api/v1/openingHours")
                .param("id", "2010")
                .param("ccid", "1")
				.param("name", "modified opening hours")
        		.param("start", "2021-04-08T11:00:00.000Z")
				.param("end", "2021-04-08T12:00:00.000Z"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	//@Test
	public void updateOpeningHours() throws Exception {

		this.mockMvc.perform(put("/api/v1/openingHours")
                .param("id", "2010")
                .param("ccid", "1")
				.param("name", "modified opening hours")
        		.param("start", "2021-04-08T11:00:00.000Z")
				.param("end", "2021-04-08T12:00:00.000Z"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	//@Test
	public void getOpeningHoursEvents() throws Exception {

		this.mockMvc.perform(get("/api/v1/openingHours/range")
                .param("ccid", "1")
        		.param("start", "2021-04-08T11:00:00.000Z")
				.param("end", "2021-06-08T12:00:00.000Z"))
				.andDo(print()).andExpect(status().isOk());
	}
		
	//@Test
	public void getLoBList() throws Exception {

		this.mockMvc.perform(get("/api/v1/ToDR/LOB/list")
                .param("userid", "nigelw@bt.com"))
				.andDo(print()).andExpect(status().isOk());
	}
		
	//@Test
	public void getScheduleList() throws Exception {

		this.mockMvc.perform(get("/api/v1/ToDR/schedule/list")
                .param("lobid", "1"))
				.andDo(print()).andExpect(status().isOk());
	}
	
	//@Test
	public void getOpeningHours() throws Exception {

		this.mockMvc.perform(put("/api/v1/ToDR/schedule/update")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
				.content("{\"name\":\"DemoNigel\",\"id\":\"5001\",\"description\":\"New text\",\"type\":1,\"confStatus\":1,\"confStatusReason\":null,\"runTimeStatus\":2,\"runTimeStatusReason\":\"Week Day open reason\",\"timeZoneId\":\"5055\",\"timeZoneDisplayName\":null,\"openingHours\":[{\"id\":\"5001\",\"description\":\"Test\",\"day\":-1,\"status\":1,\"statusReason\":null,\"open\":\"2021-07-05T03:26:00\",\"close\":\"2021-07-05T04:16:00\"},{\"id\":\"5001\",\"description\":\"Monday\",\"day\":1,\"status\":null,\"statusReason\":null,\"open\":\"1970-01-01T20:20:00\",\"close\":\"1970-01-01T23:00:00\"}]}")
				).andDo(print()).andExpect(status().isOk());
	}
		
	//@Test
	public void deleteOpeningHours() throws Exception {

		this.mockMvc.perform(delete("/api/v1/ToDR")
                .param("id", "2009")).andDo(print()).andExpect(status().isOk());
	}
}
