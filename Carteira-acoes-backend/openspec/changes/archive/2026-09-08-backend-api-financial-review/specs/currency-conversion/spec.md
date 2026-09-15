## ADDED Requirements

### Requirement: Conversão monetária sem mistura silenciosa
Valores originais em BRL ou USD SHALL permanecer identificáveis por moeda; equivalentes BRL, taxa e timestamp SHALL ser nulos ou ausentes quando não houver taxa válida, sem substituir ausência por zero.

#### Scenario: Câmbio indisponível
- **WHEN** não existe taxa USD/BRL válida para uma posição americana
- **THEN** os valores USD permanecem disponíveis e os campos BRL dependentes não são fabricados

