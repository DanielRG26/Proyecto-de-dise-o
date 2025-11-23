package com.labturnos;

import com.labturnos.repository.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
class LabTurnosApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private LabRepository labRepository;

	@MockBean
	private EquipmentRepository equipmentRepository;

	@MockBean
	private ReservationRepository reservationRepository;

	@MockBean
	private NotificationRepository notificationRepository;

	@Test
	void contextLoads() {
	}

}
