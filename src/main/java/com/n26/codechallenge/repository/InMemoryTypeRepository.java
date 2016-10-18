package com.n26.codechallenge.repository;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A implementation based on a {@link MultiValueMap}
 */
public class InMemoryTypeRepository implements TypeRepository {

    private final MultiValueMap<String, Long> map = new LinkedMultiValueMap<>();

    @Override
    public synchronized void addTransaction(String type, Long id) {
        map.add(type, id);
    }

    /**
     * Ensure no duplicates are returned by wrapping the result with a {@link HashSet}
     * @param type
     * @return
     */
    @Override
    public synchronized Set<Long> getTransactionIdsByType(String type) {
        List<Long> allEntries = map.get(type);
        if(allEntries != null) {
            return new HashSet<>(allEntries);
        } else {
            return Collections.emptySet();
        }
    }
}
