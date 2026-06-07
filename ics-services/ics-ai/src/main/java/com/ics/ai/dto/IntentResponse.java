package com.ics.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 意图分类响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResponse {

    /**
     * 主要意图
     */
    private String intent;

    /**
     * 置信度
     */
    private Double confidence;

    /**
     * 所有候选意图
     */
    private List<IntentCandidate> candidates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntentCandidate {
        private String intent;
        private Double confidence;
    }
}
