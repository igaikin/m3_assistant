package io.github.m3_assistant.service;

import io.github.m3_assistant.model.*;
import io.github.m3_assistant.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Указываем, что это сервис
public class UserService {

private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final QualificationClassRepository qualificationClassRepository;
private final TrainRepository trainRepository;
private final ProfessionRepository professionRepository;

// Внедряем зависимости через конструктор
public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                   QualificationClassRepository qualificationClassRepository, TrainRepository trainRepository, ProfessionRepository professionRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.qualificationClassRepository = qualificationClassRepository;
    this.trainRepository = trainRepository;
    this.professionRepository = professionRepository;

}

@Transactional
public void registerUser(User user, String roleName, String qualificationClassName, String trainName, String professionName) {
    // Шифруем пароль
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    // Находим роль из RoleRepository
    Role role = roleRepository.findByRole(roleName)
            .orElseThrow(() -> new RuntimeException("Роль не найдена"));
    user.setRole(role);
    //Находим класс
    QualificationClass qualificationClass = qualificationClassRepository.findByQualificationClass(qualificationClassName)
            .orElseThrow(() -> new RuntimeException("Класс не найден"));
    user.setQualificationClass(qualificationClass);
    //Находим состав
    Train train = trainRepository.findByTrain(trainName)
            .orElseThrow(() -> new RuntimeException("Состав не найден"));
    user.setTrain(train);
    Profession profession = professionRepository.findByProfession(professionName)
            .orElseThrow(() -> new RuntimeException("профессия не найдена"));
    user.setProfession(profession);
    userRepository.save(user);
}
}