package com.parkyc.poelens.ai.service;

import com.parkyc.poelens.build.domain.dto.BuildFacts;
import com.parkyc.poelens.build.domain.dto.Mechanic;
import com.parkyc.poelens.build.domain.dto.NarrativeResult;
import com.parkyc.poelens.build.domain.dto.OffenceFact;
import com.parkyc.poelens.build.domain.dto.OperationFlow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Component
public class NarrativeSectionValidator {
    public NarrativeResult validate(Map<String, Object> narrative, BuildFacts facts) {
        return validate(narrative, facts, List.of());
    }

    public NarrativeResult validate(Map<String, Object> narrative, BuildFacts facts, List<OperationFlow> operationFlows) {
        return new NarrativeResult(
                requiredString(narrative.get("buildSummary")),
                offenceSections(narrative.get("offenceSections"), facts, operationFlows),
                defenceSections(narrative.get("defenceSections"), facts),
                buffSections(narrative.get("buffSections"), facts));
    }

    private List<Mechanic> offenceSections(Object value, BuildFacts facts, List<OperationFlow> operationFlows) {
        Map<String, Set<String>> evidence = new HashMap<>();
        Set<String> sharedEvidence = sourceNames(facts);
        for (OffenceFact attack : facts.offence() == null ? List.<OffenceFact>of() : facts.offence()) {
            Set<String> values = new HashSet<>(sharedEvidence);
            values.add(attack.name());
            evidence.put(attack.name(), values);
        }
        List<?> sections = requiredSections(value, evidence.isEmpty());

        List<Mechanic> result = new ArrayList<>();
        Set<String> coveredAttacks = new HashSet<>();
        for (Object sectionValue : sections) {
            if (!(sectionValue instanceof Map<?, ?> section)) throw invalidResponse();
            String attackName = string(section.get("attackName"));
            String kind = string(section.get("section"));
            String explanation = string(section.get("explanation"));
            if (attackName == null || kind == null || !Set.of("core", "supports", "modifiers", "operation").contains(kind)
                    || explanation == null || !(section.get("evidence") instanceof List<?> references) || references.isEmpty()
                    || !(section.get("flowSubjects") instanceof List<?> flowSubjects) || !evidence.containsKey(attackName)) throw invalidResponse();
            if (!references.contains(attackName)) throw invalidResponse();
            if ("operation".equals(kind) && flowSubjects.isEmpty() && !hasSkillEffects(attackName, facts)) throw invalidResponse();
            for (Object reference : references) {
                if (!(reference instanceof String name) || !evidence.get(attackName).contains(name)) throw invalidResponse();
            }
            Set<String> groundedFlowSubjects = groundedFlowSubjects(attackName, facts, operationFlows);
            for (Object flowSubject : flowSubjects) {
                if (!(flowSubject instanceof String subject) || !groundedFlowSubjects.contains(subject)) throw invalidResponse();
            }
            result.add(new Mechanic(offenceSectionTitle(kind, attackName), explanation));
            coveredAttacks.add(attackName);
        }
        if (!coveredAttacks.containsAll(evidence.keySet())) throw invalidResponse();
        return result;
    }

    static Set<String> sourceNames(BuildFacts facts) {
        Set<String> names = new HashSet<>();
        if (facts.skills() != null) facts.skills().stream().filter(skill -> Boolean.TRUE.equals(skill.enabled())).forEach(skill -> {
            names.add(skill.name());
            if (skill.supports() != null) skill.supports().stream().filter(support -> Boolean.TRUE.equals(support.enabled()))
                    .forEach(support -> names.add(support.name()));
        });
        if (facts.items() != null) facts.items().forEach(item -> names.add(item.name()));
        if (facts.jewels() != null) facts.jewels().forEach(jewel -> names.add(jewel.name()));
        if (facts.passives() != null) facts.passives().forEach(passive -> names.add(passive.name()));
        if (facts.ascendancies() != null) facts.ascendancies().forEach(node -> names.add(node.name()));
        if (facts.buffs() != null) facts.buffs().forEach(buff -> names.add(buff.name()));
        if (facts.offence() != null) facts.offence().forEach(attack -> names.add(attack.name()));
        names.remove(null);
        names.remove("");
        return names;
    }

    private boolean hasSkillEffects(String attackName, BuildFacts facts) {
        return facts.skills() != null && facts.skills().stream().anyMatch(skill -> attackName.equals(skill.name())
                && Boolean.TRUE.equals(skill.enabled()) && skill.effects() != null && !skill.effects().isEmpty());
    }

    private Set<String> groundedFlowSubjects(String attackName, BuildFacts facts, List<OperationFlow> operationFlows) {
        if (operationFlows == null) return Set.of();
        Set<String> subjects = new HashSet<>();
        for (OperationFlow flow : operationFlows) {
            if (flow == null || !hasText(flow.subject())) continue;
            if (attackName.equals(flow.subject()) || (flow.grounds() != null && flow.grounds().stream()
                    .anyMatch(ground -> ground != null && attackName.equals(ground.sourceName())))) {
                subjects.add(flow.subject());
            }
        }
        return subjects;
    }

    private List<Mechanic> defenceSections(Object value, BuildFacts facts) {
        Set<String> evidence = new HashSet<>();
        if (facts.defence() != null) facts.defence().forEach(fact -> evidence.add(fact.kind()));
        return structuredSections(value, "defenceKind", Set.of("resource", "mitigation", "avoidance", "recovery"), evidence,
                this::defenceSectionTitle);
    }

    private List<Mechanic> buffSections(Object value, BuildFacts facts) {
        Map<String, Set<String>> evidence = new HashMap<>();
        if (facts.buffs() != null) facts.buffs().forEach(buff -> {
            if (buff.tags() == null || buff.tags().isEmpty()) return;
            Set<String> values = new HashSet<>();
            values.add(buff.name());
            values.addAll(buff.tags());
            evidence.put(buff.name(), values);
        });
        return structuredSections(value, "buffName", Set.of("offence", "defence", "utility"), evidence, this::buffSectionTitle);
    }

    private List<Mechanic> structuredSections(Object value, String subjectKey, Set<String> kinds, Set<String> evidence,
                                               BiFunction<String, String, String> title) {
        Map<String, Set<String>> subjects = new HashMap<>();
        evidence.forEach(subject -> subjects.put(subject, Set.of(subject)));
        return structuredSections(value, subjectKey, kinds, subjects, title);
    }

    private List<Mechanic> structuredSections(Object value, String subjectKey, Set<String> kinds, Map<String, Set<String>> evidence,
                                               BiFunction<String, String, String> title) {
        List<?> sections = requiredSections(value, evidence.isEmpty());
        List<Mechanic> result = new ArrayList<>();
        Set<String> coveredSubjects = new HashSet<>();
        for (Object sectionValue : sections) {
            if (!(sectionValue instanceof Map<?, ?> section)) throw invalidResponse();
            String subject = string(section.get(subjectKey));
            String kind = string(section.get("section"));
            String explanation = string(section.get("explanation"));
            if (subject == null || kind == null || !kinds.contains(kind) || explanation == null
                    || !(section.get("evidence") instanceof List<?> references) || references.isEmpty() || !evidence.containsKey(subject)) throw invalidResponse();
            boolean containsSubject = false;
            for (Object reference : references) {
                if (!(reference instanceof String name) || !evidence.get(subject).contains(name)) throw invalidResponse();
                containsSubject |= subject.equals(name);
            }
            if (!containsSubject) throw invalidResponse();
            result.add(new Mechanic(title.apply(kind, subject), explanation));
            coveredSubjects.add(subject);
        }
        if (!coveredSubjects.containsAll(evidence.keySet())) throw invalidResponse();
        return result;
    }

    private String offenceSectionTitle(String kind, String attackName) {
        return switch (kind) {
            case "core" -> "공격이 작동하는 과정: " + attackName;
            case "supports" -> "보조젬 연결: " + attackName;
            case "modifiers" -> "핵심 상호작용: " + attackName;
            case "operation" -> "운용 방식: " + attackName;
            default -> throw new IllegalArgumentException("알 수 없는 공격 섹션입니다: " + kind);
        };
    }

    private String defenceSectionTitle(String kind, String defenceKind) {
        return switch (kind) {
            case "resource" -> "방어 자원: " + defenceKind;
            case "mitigation" -> "피해 경감: " + defenceKind;
            case "avoidance" -> "회피·막기: " + defenceKind;
            case "recovery" -> "회복: " + defenceKind;
            default -> throw new IllegalArgumentException("알 수 없는 방어 섹션입니다: " + kind);
        };
    }

    private String buffSectionTitle(String kind, String buffName) {
        return switch (kind) {
            case "offence" -> "공격 버프: " + buffName;
            case "defence" -> "방어 버프: " + buffName;
            case "utility" -> "유틸리티 버프: " + buffName;
            default -> throw new IllegalArgumentException("알 수 없는 버프 섹션입니다: " + kind);
        };
    }

    private String requiredString(Object value) {
        String result = string(value);
        if (result == null) throw invalidResponse();
        return result;
    }

    private List<?> requiredSections(Object value, boolean mayBeEmpty) {
        if (!(value instanceof List<?> sections) || (!mayBeEmpty && sections.isEmpty())) throw invalidResponse();
        return sections;
    }

    private IllegalArgumentException invalidResponse() {
        return new IllegalArgumentException("OpenAI 분석 응답 검증에 실패했습니다.");
    }

    private String string(Object value) {
        return value instanceof String text && !text.isBlank() ? text : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
