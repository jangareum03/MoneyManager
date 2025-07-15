package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.QnADao;
import com.areum.moneymanager.dto.response.main.FaqResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class FaqService {

	private final Logger logger = LogManager.getLogger(this);


	/**
	 * 특정 json파일의 내용을 리스트로 담아 반환합니다.
	 *
	 * @return	FAQ의 질문과 답변 리스트
	 * @throws IOException
	 */
	public List<FaqResponseDTO.Read> getFqaList() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = getClass().getResourceAsStream("/static/data/faq.json");

		return Arrays.asList( mapper.readValue( is, FaqResponseDTO.Read[].class) );
	}
}
