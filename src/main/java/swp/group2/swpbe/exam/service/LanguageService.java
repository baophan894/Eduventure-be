package swp.group2.swpbe.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.LanguageDTO;
import swp.group2.swpbe.exam.entities.Language;
import swp.group2.swpbe.exam.repository.LanguageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<LanguageDTO> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LanguageDTO convertToDTO(Language language) {
        return new LanguageDTO(
                language.getId(),
                language.getCode(),
                language.getName());
    }
}