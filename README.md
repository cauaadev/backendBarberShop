# Nahora API

API para gestão de negócios que trabalham com horário marcado, como barbearias, salões e clínicas de estética. Cada negócio tem seus próprios clientes, serviços, equipe e agenda.

Tecnologias usadas no projeto: Spring Boot, Spring JPA, Hibernate, Spring Security
Deploy foi feito na AWS com EC2 configurada localmente : http://13.223.230.145

## Funcionalidades

- Cadastro de negócios com período de teste e planos
- Login por e-mail ou telefone
- Equipe com perfis de dono e profissional, e comissão por profissional
- Agenda com duração dos serviços e bloqueio de horário ocupado
- Página pública de agendamento com os horários livres de cada profissional
- Histórico de clientes com visitas, valor gasto e faltas
- Planos mensais para clientes com controle de créditos e pagamentos
- Painel com faturamento, ticket médio, faltas e comissões
