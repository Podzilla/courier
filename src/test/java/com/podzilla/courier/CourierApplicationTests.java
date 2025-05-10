package com.podzilla.courier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.podzilla.courier.controllers.CourierController;
import com.podzilla.courier.dtos.couriers.CreateCourierRequestDto;
import com.podzilla.courier.dtos.couriers.UpdateCourierRequestDto;
import com.podzilla.courier.dtos.couriers.CourierResponseDto;
import com.podzilla.courier.models.CourierStatus;
import com.podzilla.courier.services.CourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourierControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CourierService courierService;

	@InjectMocks
	private CourierController courierController;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(courierController)
				.build();
	}

	@Test
	@DisplayName("GET /couriers → 200 OK with list of couriers")
	void getAllCouriers() throws Exception {
		List<CourierResponseDto> mockList = List.of(
				new CourierResponseDto("1", "Alice", "0123456789", CourierStatus.AVAILABLE),
				new CourierResponseDto("2", "Bob",   "0987654321", CourierStatus.OFFLINE)
		);
		Mockito.when(courierService.getAllCouriers()).thenReturn(mockList);

		mockMvc.perform(get("/couriers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is("Alice")))
				.andExpect(jsonPath("$[1].status", is("OFFLINE")));
	}

	@Test
	@DisplayName("GET /couriers/{id} → 200 OK when found")
	void getCourierByIdFound() throws Exception {
		CourierResponseDto dto = new CourierResponseDto("42", "Charlie", "0111222333", CourierStatus.ASSIGNED);
		Mockito.when(courierService.getCourierById("42")).thenReturn(Optional.of(dto));

		mockMvc.perform(get("/couriers/42"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is("42")))
				.andExpect(jsonPath("$.status", is("ASSIGNED")));
	}

	@Test
	@DisplayName("GET /couriers/{id} → 404 Not Found when missing")
	void getCourierByIdNotFound() throws Exception {
		Mockito.when(courierService.getCourierById("nope")).thenReturn(Optional.empty());

		mockMvc.perform(get("/couriers/nope"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST /couriers → 200 OK with created courier")
	void createCourier() throws Exception {
		CreateCourierRequestDto req = new CreateCourierRequestDto("Daisy", "0222333444");
		CourierResponseDto resp = new CourierResponseDto("100", "Daisy", "0222333444", CourierStatus.PICKED_UP);

		Mockito.when(courierService.createCourier(Mockito.any(CreateCourierRequestDto.class))).thenReturn(resp);

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is("100")))
				.andExpect(jsonPath("$.status", is("PICKED_UP")));
	}

	@Test
	@DisplayName("PUT /couriers/{id} → 200 OK when update succeeds")
	void updateCourierFound() throws Exception {
		String json = """
            {
              "id":"55",
              "name":"Elle",
              "mobileNo":"0333444555",
              "status":"ON_BREAK"
            }
            """;
		CourierResponseDto updated = new CourierResponseDto("55","Elle","0333444555",CourierStatus.ON_BREAK);

		Mockito.when(courierService.updateCourier(eq("55"), Mockito.any(UpdateCourierRequestDto.class)))
				.thenReturn(Optional.of(updated));

		mockMvc.perform(put("/couriers/55")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("ON_BREAK")))
				.andExpect(jsonPath("$.name", is("Elle")));
	}

	@Test
	@DisplayName("PUT /couriers/{id} → 404 Not Found when update misses")
	void updateCourierNotFound() throws Exception {
		String json = """
            { "id":"nope", "status":"DELIVERING" }
            """;

		Mockito.when(courierService.updateCourier(eq("nope"), Mockito.any(UpdateCourierRequestDto.class)))
				.thenReturn(Optional.empty());

		mockMvc.perform(put("/couriers/nope")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json)
				)
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /couriers/{id} → 200 OK when delete succeeds")
	void deleteCourierFound() throws Exception {
		CourierResponseDto dto = new CourierResponseDto("66","George","0444555666",CourierStatus.UNAVAILABLE);
		Mockito.when(courierService.deleteCourier("66")).thenReturn(Optional.of(dto));

		mockMvc.perform(delete("/couriers/66"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("UNAVAILABLE")));
	}

	@Test
	@DisplayName("DELETE /couriers/{id} → 404 Not Found when delete misses")
	void deleteCourierNotFound() throws Exception {
		Mockito.when(courierService.deleteCourier("none")).thenReturn(Optional.empty());

		mockMvc.perform(delete("/couriers/none"))
				.andExpect(status().isNotFound());
	}
}