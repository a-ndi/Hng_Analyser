package com.hng.Analyser.Repo;


import com.hng.Analyser.Model.AnalysedString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyserRepo extends JpaRepository<AnalysedString, Integer> {

    Optional<AnalysedString> findByValue(String value);

}
