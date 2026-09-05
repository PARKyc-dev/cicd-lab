package com.parkyc.poelens.build.domain.dto;

import java.util.List;

public record ItemFact(
        String slot,
        String name,
        String baseName,
        String rarity,
        List<String> modifiers,
        List<String> tags) {
}
