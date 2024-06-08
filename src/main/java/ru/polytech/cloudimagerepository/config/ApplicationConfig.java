package ru.polytech.cloudimagerepository.config;

import dev.brachtendorf.jimagehash.hashAlgorithms.AverageHash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${hashing-algorithm.bit-resolution}")
    private int bitResolution;

    @Bean
    public HashingAlgorithm hashingAlgorithm() {
        return new PerceptiveHash(bitResolution);
    }
}
