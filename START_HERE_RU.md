# Grigoriev Yanis — быстрый запуск DevOps Midterm

В архиве готовые исходники Spring Boot CRUD API, PostgreSQL Compose, Jenkinsfile, Bash-скрипты, Git-ветки и история настоящего merge conflict. Подробные объяснения каждого этапа — в `RUNBOOK_RU.md`.

## 1. Ubuntu WSL2: пакеты
```bash
sudo apt update
sudo apt install -y git gh curl unzip python3 openssl openjdk-21-jdk docker.io docker-compose-v2
sudo service docker start
sudo usermod -aG docker "$USER"
newgrp docker
docker ps
```

## 2. Распаковка
```bash
unzip Grigoriev_Yanis_Student_API_Midterm.zip
cd grigoriev-yanis-student-api
```

## 3. Linux, лог, права, резервная копия
```bash
bash scripts/run_linux_tasks.sh
cat Grigoriev_Yanis_log_analysis.txt
ls -l scripts/Grigoriev_Yanis_system_report.sh
```

## 4. Git-ветки, конфликты, версия
```bash
git log --graph --oneline --all --decorate
git branch -a
git tag -l
```

## 5. GitHub (новый пустой репозиторий)
```bash
git config user.email 'ТВОЙ_РЕАЛЬНЫЙ_EMAIL'
gh auth login
gh repo create grigoriev-yanis-student-api --public --source=. --remote=origin --push
git push origin feature/grigoriev-yanis-crud feature/grigoriev-yanis-documentation feature/grigoriev-yanis-ci-failure
git push origin v1.0-grigoriev-yanis
```

## 6. Docker + PostgreSQL
```bash
./scripts/create_env.sh
docker compose up -d --build
docker compose ps
curl http://localhost:18080/api/about
./scripts/test_crud_and_persistence.sh
```

## 7. Jenkins
Создать Job `Grigoriev_Yanis_Student_API` → Pipeline script from SCM → URL своего репозитория → ветка `*/main` → файл `Jenkinsfile` → Build Now. Далее выполнить задания 4.3 и 4.4 по `RUNBOOK_RU.md`.

**Важное:** GitHub, Docker Compose и Jenkins требуют реального выполнения команд и живых скриншотов. Наличие Jenkinsfile не означает, что Pipeline уже выполнялся.

## 8. Запись/повтор терминала (по желанию)
```bash
mkdir -p ~/midterm_recording
cd ~/midterm_recording
script --log-timing timing.log session.log
# в новой сессии перейти в проект и выполнить команды демонстрации
exit
scriptreplay --log-timing timing.log session.log
```
`scriptreplay` только воспроизводит сохранённый терминальный вывод: команды не выполняются повторно.
