# Photo vocabulary dataset

사용자가 제공한 사진 16장에서 확인한 표제어 142개를 정리한 데이터셋입니다.

- `words.csv`는 `Word`에 대응합니다.
- `word_means.csv`는 `WordMean`에 대응하며, `source_key`와 `display_order`로 단어와 뜻을 연결합니다.
- `word_sentences.csv`는 `WordSentence`용 예문 초안입니다. 사진 속 예문을 복사하지 않고 새로 작성했습니다.

`source_key`는 CSV 내부 조인용 키입니다. 데이터 적재 시 `Word`를 먼저 저장한 뒤, 각 키에 대응하는 생성된 `word_id`를 `WordMean`과 `WordSentence`에 연결하면 됩니다.

현재 `WordSentence` 엔티티에는 문장 본문, 번역, 표시 순서 필드가 없으므로 `word_sentences.csv`의 `sentence`, `sentence_meaning`, `display_order`를 실제 저장하려면 해당 필드를 엔티티에 추가해야 합니다.
