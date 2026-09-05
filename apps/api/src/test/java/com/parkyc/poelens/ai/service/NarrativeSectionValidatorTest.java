package com.parkyc.poelens.ai.service;

import com.parkyc.poelens.build.domain.dto.AppliedModifierFact;
import com.parkyc.poelens.build.domain.dto.BuffFact;
import com.parkyc.poelens.build.domain.dto.BuildFacts;
import com.parkyc.poelens.build.domain.dto.DefenceFact;
import com.parkyc.poelens.build.domain.dto.Mechanic;
import com.parkyc.poelens.build.domain.dto.OffenceFact;
import com.parkyc.poelens.build.domain.dto.OperationFact;
import com.parkyc.poelens.build.domain.dto.OperationFlow;
import com.parkyc.poelens.build.domain.dto.PerformanceFact;
import com.parkyc.poelens.build.domain.dto.SkillFact;
import com.parkyc.poelens.build.domain.dto.SupportGemFact;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NarrativeSectionValidatorTest {
    private final NarrativeSectionValidator validator = new NarrativeSectionValidator();

    @Test
    void returnsOnlyGroundedAiNarratives() {
        var result = validator.validate(validNarrative(), facts());

        assertThat(result.summary()).isEqualTo("AI가 생성한 빌드 요약입니다.");
        assertThat(result.offence()).containsExactly(new Mechanic("보조젬 연결: Fire Trap", "Burning Damage로 피해를 강화합니다."));
        assertThat(result.defence()).containsExactly(new Mechanic("피해 경감: armour", "방어도로 물리 피해를 줄입니다."));
        assertThat(result.buffs()).containsExactly(new Mechanic("방어 버프: Determination", "방어도를 높입니다."));
    }

    @Test
    void rejectsAnIncompleteAiNarrativeInsteadOfUsingRuleBasedText() {
        assertThatThrownBy(() -> validator.validate(Map.of(
                "buildSummary", "AI 요약", "offenceSections", List.of(),
                "defenceSections", List.of(), "buffSections", List.of()), facts()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OpenAI 분석 응답 검증에 실패했습니다.");
    }

    @Test
    void rejectsUnknownEvidence() {
        Map<String, Object> narrative = new java.util.HashMap<>(validNarrative());
        narrative.put("offenceSections", List.of(Map.of(
                "attackName", "Fire Trap", "section", "modifiers", "explanation", "근거 없는 설명",
                "evidence", List.of("Unknown Modifier"), "flowSubjects", List.of())));

        assertThatThrownBy(() -> validator.validate(narrative, facts()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsAFlowGroundedToTheAttack() {
        OperationFact consume = new OperationFact("skill", "Fire Trap", "consume", "frenzy-charge", List.of("Consumes a Frenzy Charge"));
        OperationFlow flow = new OperationFlow("frenzy-charge", List.of("소비"), List.of(consume));
        Map<String, Object> narrative = new java.util.HashMap<>(validNarrative());
        narrative.put("offenceSections", List.of(Map.of(
                "attackName", "Fire Trap", "section", "operation", "explanation", "격분 충전을 소비합니다.",
                "evidence", List.of("Fire Trap"), "flowSubjects", List.of("frenzy-charge"))));

        var result = validator.validate(narrative, facts(), List.of(flow));

        assertThat(result.offence()).containsExactly(new Mechanic("운용 방식: Fire Trap", "격분 충전을 소비합니다."));
    }

    @Test
    void schemaRestrictsAttackNamesAndEvidenceToExistingSources() {
        var schema = new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(new OpenAiNarrativeSchema().create(facts(), List.of()));
        var properties = schema.path("properties").path("offenceSections").path("items").path("anyOf").get(0).path("properties");

        assertThat(properties.path("attackName").path("enum").toString()).isEqualTo("[\"Fire Trap\"]");
        assertThat(properties.path("evidence").path("items").path("enum").toString())
                .contains("Fire Trap", "Burning Damage", "Determination");
    }

    private Map<String, Object> validNarrative() {
        return Map.of(
                "buildSummary", "AI가 생성한 빌드 요약입니다.",
                "offenceSections", List.of(Map.of(
                        "attackName", "Fire Trap", "section", "supports", "explanation", "Burning Damage로 피해를 강화합니다.",
                        "evidence", List.of("Fire Trap", "Burning Damage"), "flowSubjects", List.of())),
                "defenceSections", List.of(Map.of(
                        "defenceKind", "armour", "section", "mitigation", "explanation", "방어도로 물리 피해를 줄입니다.",
                        "evidence", List.of("armour"))),
                "buffSections", List.of(Map.of(
                        "buffName", "Determination", "section", "defence", "explanation", "방어도를 높입니다.",
                        "evidence", List.of("Determination"))));
    }

    private BuildFacts facts() {
        return new BuildFacts(
                List.of(new OffenceFact("Fire Trap", "primary", 1.0, "trap", List.of(),
                        List.of(new AppliedModifierFact("Fire Damage", "INC", "Passive", false)))),
                List.of(new SkillFact("Fire Trap", 20, 0, "Default", true, false,
                        List.of(new SupportGemFact("Burning Damage", 20, 0, "Default", true, false, List.of())))),
                List.of(new DefenceFact("armour", 21_000.0)),
                List.of(new BuffFact("Determination", "aura", "player", List.of("armour"))),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                new PerformanceFact(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    }
}
