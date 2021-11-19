package com.mockcompany.webapp.controller;

import com.mockcompany.webapp.api.SearchReportResponse;
import com.mockcompany.webapp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Management decided it is super important that we have lots of products that match the following terms.
 * So much so, that they would like a daily report of the number of products for each term along with the total
 * product count.
 */
@RestController
public class ReportController {

    // After reading code/tests, we can capture the important terms in an array!
    private static final String[] importantTerms = new String[] {
            "Cool",
            "Amazing",
            "Perfect",
            "Kids"
    };

    private final EntityManager entityManager;
    // Declare SearchService same as EntityManager
    private final SearchService searchService;

    // Add the SearchService to the constructor
    @Autowired
    public ReportController(EntityManager entityManager, SearchService searchService) {
        this.entityManager = entityManager;
        this.searchService = searchService;
    }

    @GetMapping("/api/products/report")
    public SearchReportResponse runReport() {
        // We could use the search service and do an empty string query to get the count but this is much more efficient
        Number count = (Number) this.entityManager.createQuery("SELECT count(item) FROM ProductItem item").getSingleResult();

        // For each important term, query on it and add size of results to our Map
        Map<String, Integer> hits = new HashMap<>();
        for (String term : importantTerms) {
            hits.put(term, searchService.search(term).size());
        }

        // Transform to API response and return
        SearchReportResponse response = new SearchReportResponse();
        response.setProductCount(count.intValue());
        response.setSearchTermHits(hits);
        return response;
    }
}
