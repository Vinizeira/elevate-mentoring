# Regras do contexto Scheduling

Saída do Event Storming (FND-10). Desenho completo: `event-storming-context-map.excalidraw`.

**Como ler cada evento:**
- **Agregado:** quem é dono da regra (vira a classe principal).
- **Regras:** quando o sistema diz **NÃO**. Cada uma vira um `if` + exceção.
- **Reações:** o que o sistema faz **sozinho** depois (políticas). Uma pessoa decidir algo depois é um **novo comando**, não reação.

---

## SessionBooked — o aluno agendou uma sessão

- **Comando:** `BookSession` (ator: Aluno)
- **Agregado:** `Session`
- **Regras (NÃO quando…):**
  - o horário não existe na agenda do mentor
  - o horário já foi ocupado por outro aluno
  - o horário está no passado
  - o aluno já tem outra sessão no mesmo horário
- **Reações:**
  - **Notification:** e-mail de confirmação para aluno e mentor
  - **Notification:** agendar lembrete 24h antes

## SessionCancelled — aluno ou mentor cancelou

- **Comando:** `CancelSession` (ator: Aluno ou Mentor)
- **Agregado:** `Session`
- **Regras (NÃO quando…):**
  - faltam menos de 24h para a sessão
  - a sessão já aconteceu
  - a sessão já foi cancelada
  - quem pede não é o aluno nem o mentor dessa sessão
  - o motivo não foi informado
- **Reações:**
  - **Notification:** avisar a outra pessoa (e-mail / WhatsApp — o canal é detalhe da `infrastructure`)
  - **Scheduling:** o horário volta a ficar livre na agenda do mentor

## SessionCompleted ⭐ — a sessão aconteceu (confirmação dupla)

**Decisão:** a sessão só conta quando **aluno E mentor** confirmam. Isso protege a métrica ESG contra horas falsas.

Fluxo em 3 eventos:

1. `ConfirmSessionAsMentor` → **`MentorConfirmedSession`**
   - o mentor marca os **temas** que explicou (lista fixa: Currículo, Java, Entrevista…) + relato livre
2. `ConfirmSessionAsStudent` → **`StudentConfirmedSession`**
   - o aluno marca os **temas** que entendeu + relato livre ("como foi a sessão")
3. Quando os dois confirmaram **e pelo menos um tema bate** → **`SessionCompleted`**
   - se nenhum tema bate → **`SessionDisputed`** (não conta horas, vai para análise do Admin RH)

> Por que temas e não texto? Duas pessoas nunca escrevem igual — o sistema não consegue comparar texto livre. Temas de uma lista são comparáveis. O texto fica como relato.

- **Agregado:** `Session`
- **Regras (NÃO quando…):**
  - a sessão não está como agendada (foi cancelada ou já concluída)
  - o horário da sessão ainda não terminou
  - quem confirma não é o mentor/aluno dessa sessão
  - a mesma pessoa tenta confirmar duas vezes
  - nenhum tema foi marcado
  - a sessão foi marcada como falta do aluno (`StudentNoShow`)
  - passou o prazo de confirmação (72h após a sessão) → vira **`SessionConfirmationExpired`**, não conta horas
- **Reações ao `SessionCompleted`:**
  - **Recognition:** somar as horas ao mentor → se passar de 10h, `CertificateIssued`
  - **Impact:** registrar as horas de impacto para a empresa do mentor (métrica ESG)

---

## Dúvidas em aberto (decisões de negócio)

- [ ] Cancelamento com menos de 24h: proibir, ou permitir e gerar `LateCancellation`? (ex.: mentor doente 2h antes)
- [x] Quem confirma que a sessão aconteceu? → **os dois** (aluno e mentor), com temas + relato. Ver SessionCompleted.
- [ ] Prazo de confirmação: 72h é bom? Mandar lembrete para quem ainda não confirmou?
- [ ] `SessionDisputed`: quem resolve — Admin RH da empresa ou a plataforma?
- [ ] A sessão conta a duração agendada (ex.: 1h) ou a duração real?
- [ ] Quanto tempo depois do horário o mentor pode marcar `StudentNoShow`?
