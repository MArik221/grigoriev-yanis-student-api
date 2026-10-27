# Полная практическая инструкция — DevOps Midterm, Grigoriev Yanis

Имя: **Grigoriev Yanis** · Группа: **IT22303** · ID: **37038**.

> Выполнять в Ubuntu WSL 2. В этом ZIP уже есть Git-история с настоящим конфликтом и его исправлением, две feature-ветки и тег. Нужны действующие Docker, GitHub и Jenkins для получения реальных подтверждений внешних этапов. Ни один успешный сетевой build заранее не утверждается.

## 0. Подготовка (однократно)

```bash
sudo apt update
sudo apt install -y git gh curl unzip python3 openssl openjdk-21-jdk docker.io docker-compose-v2
sudo service docker start
sudo usermod -aG docker "$USER"
newgrp docker
docker ps
git --version; java -version; docker compose version
```

> Если `docker-compose-v2` отсутствует в вашей Ubuntu, установите Docker Compose plugin по официальной инструкции Docker или включите `universe` и повторите установку. `docker ps` должен работать без permission denied. Группа `docker` даёт фактически root-доступ.

Распаковать ZIP в домашнюю папку Linux (через `explorer.exe .` можно скопировать ZIP из Windows в WSL). В папке с ZIP:

```bash
unzip Grigoriev_Yanis_Student_API_Midterm.zip
cd grigoriev-yanis-student-api
```

Если видите `not a git repository`, значит из архива не извлекли скрытую `.git`.

## 1. Linux — задания 1.1–1.4

Создание структуры было выполнено терминальными командами `mkdir -p ...`; каталоги `scripts logs backups src` и файлы `Dockerfile docker-compose.yml Jenkinsfile build.gradle README.md` находятся в архиве. Демонстрация:

```bash
pwd
find . -maxdepth 2 -type d -not -path './.git*' | sort
ls -la
find scripts logs backups src -maxdepth 2 -type f | sort
bash scripts/run_linux_tasks.sh
ls -l scripts/Grigoriev_Yanis_system_report.sh
ls -l logs/Grigoriev_Yanis_system_report.log backups/Grigoriev_Yanis_README_backup.md
cat Grigoriev_Yanis_log_analysis.txt
cmp README.md backups/Grigoriev_Yanis_README_backup.md
ps aux | head
pgrep -af java || true
```

`run_linux_tasks.sh` сам создаёт пустой лог, устанавливает `chmod 744` (владелец `rwx`, остальные `r--`), запускает скрипт, создаёт копию README и сохраняет найденную фамилию в `Grigoriev_Yanis_log_analysis.txt`.

`>` перезаписывает файл, `>>` дописывает в конец; системный скрипт использует `tee -a`, чтобы и показывать, и дописывать отчёт.

## 2. Git — задания 2.1–2.3

История собрана локально и вложена в ZIP. Укажи **свой реальный email** для будущих коммитов (в подготовленной истории технический email-заглушка):

```bash
git config user.name 'Grigoriev Yanis'
git config user.email 'ТВОЯ_НАСТОЯЩАЯ_ПОЧТА'
git status
git branch -a
git log --graph --oneline --all --decorate
cat .gitignore
git show --stat 'feature/grigoriev-yanis-crud'
git show --stat 'feature/grigoriev-yanis-documentation'
git show --stat ebc96af
```

Последняя команда демонстрирует коммит с ручным решением реального конфликта в `README.md`. В истории есть начальный коммит, два коммита CRUD, слияние CRUD-ветки, два несовместимых изменения одной строки README на разных ветках и завершающий merge-commit с разрешением конфликта.

Для демонстрации создания ветки своими руками без порчи истории:

```bash
git switch -c feature/grigoriev-yanis-demo
git switch main
git branch -d feature/grigoriev-yanis-demo
```

## 3. GitHub — задание 2.4

```bash
gh auth login
gh auth status
cd ~/grigoriev-yanis-student-api   # путь может отличаться
# Создаём ПУСТОЙ новый публичный репозиторий и отправляем текущую ветку:
gh repo create grigoriev-yanis-student-api --public --source=. --remote=origin --push
# Отправляем ветки отдельно:
git push -u origin feature/grigoriev-yanis-crud
git push -u origin feature/grigoriev-yanis-documentation
# Отправляем тег:
git push origin v1.0-grigoriev-yanis
# Проверяем:
git remote -v
git branch -a
git tag -l
git log --graph --oneline --all
gh repo view --web
```

Если репозиторий уже существует на GitHub, `gh repo create` **не повторять**:

```bash
git remote add origin https://github.com/ТВОЙ_ЛОГИН/grigoriev-yanis-student-api.git
git push -u origin main
git push origin feature/grigoriev-yanis-crud feature/grigoriev-yanis-documentation
git push origin v1.0-grigoriev-yanis
```

`.env` содержит пароль БД и специально находится в `.gitignore`. На GitHub должен попасть только `.env.example` без настоящих секретов.

## 4. Spring Boot + Docker — задания 3.1–3.3

Создать локальный пароль БД (НЕ загружать `.env` в GitHub):

```bash
./scripts/create_env.sh
cat .env.example  # образец без пароля
ls -l .env         # сам пароль на видео НЕ показывать
```

Первичная проверка Java-кода выполняется Gradle Wrapper (для первого запуска нужен Интернет):

```bash
./gradlew --no-daemon clean test bootJar
```

`gradlew` скачивает недостающий официальный wrapper JAR, проверяя SHA256, затем скачивает Gradle 8.14.3. В Docker сборка через `gradle:8.14.3-jdk21`, поэтому локальная установка Gradle не требуется. На первом запуске JAR/зависимости могут скачиваться долго.

```bash
docker compose config --quiet
docker compose up -d --build
docker compose ps
docker images
docker image inspect grigoriev-yanis-student-api:v1.0-37038 --format '{{.Id}} {{.Size}} {{.Created}}'
docker compose logs --tail=40 api
docker compose logs --tail=20 db
docker inspect grigoriev-yanis-student-api-api-1 --format '{{.State.Status}} {{.Config.Image}}'  # имя может отличаться: сначала docker compose ps
docker compose exec api java -version
curl -fsS http://localhost:18080/api/about
curl -fsS http://localhost:18080/actuator/health
```

API подключается к БД по адресу `jdbc:postgresql://db:5432/studentsdb` — `db` это имя сервиса Compose в отдельной сети. PostgreSQL хранит данные в named volume `pgdata`, её Compose-имя зависит от имени проекта. Команда `docker compose down` **без `-v`** не удаляет данные в named volume.

Управление контейнерами:

```bash
docker ps
docker compose stop api
docker compose start api
docker compose ps
```

Пересборка после изменения Gradle версии приложения (в отдельной временной ветке; не менять main):

```bash
git switch -c feature/grigoriev-yanis-version-bump
sed -i "s/version = '1.0-37038'/version = '1.0.1-37038'/" build.gradle
git add build.gradle
git commit -m 'Grigoriev Yanis: bump API build version to 1.0.1'
docker compose build api
docker compose up -d api
git switch main
```

При переключении обратно на `main` Docker-образ не перестраивается сам. Для восстановления сборки основной версии вызови `docker compose up -d --build`.

## 5. Все API запросы + проверка PostgreSQL persistence — задание 3.4

**Один сценарий выполняет все шесть CRUD-проверок и показывает сохранение данных после перезапуска PostgreSQL и API:**

```bash
./scripts/test_crud_and_persistence.sh
```

Сценарий показывает `POST /api/students`, `GET /api/students`, `GET /api/students/{id}`, `PUT /api/students/{id}`, `DELETE /api/students/{id}` и HTTP `404` для удалённого студента. Между обновлением и удалением происходит `docker compose down` / `up`, после которого запись находится по тому же ID. Это доказательство сохранности в PostgreSQL volume.

Ручной запрос с личными данными:

```bash
curl -fsS -X POST http://localhost:18080/api/students \
  -H 'Content-Type: application/json' \
  -d '{"name":"Yanis","surname":"Grigoriev","groupCode":"IT22303","studentNumber":"37038"}'
curl -fsS http://localhost:18080/api/students
```

Если такой `studentNumber` уже есть, приложение возвращает `409 Conflict` — это правильная защита от дублей.

## 6. Jenkins — задания 4.1–4.2

Для создания/запуска Jenkins нужен **настоящий Jenkins-сервер** с доступом к Git, Java 21, Docker socket и Интернету. В проекте уже лежит полноценный `Jenkinsfile`. Он использует `checkout scm`, `./gradlew`, сборку Docker и проверку образа.

Пошаговая настройка в Jenkins UI:

1. New Item → `Grigoriev_Yanis_Student_API` → **Pipeline**.
2. Definition → **Pipeline script from SCM**; SCM → Git.
3. Repository URL → URL своего репозитория `grigoriev-yanis-student-api`.
4. Branch Specifier → `*/main`; Script Path → `Jenkinsfile`.
5. Если репозиторий приватный, добавить токен/ключ в **Manage Jenkins → Credentials**, затем выбрать его в Job. **Не записывать пароль в Jenkinsfile**.
6. Агент должен иметь Java 21, curl и Docker. Jenkinsfile скачивает Gradle через официальный Wrapper.
7. Save → Build Now → Console Output. Нужно увидеть пять этапов: Checkout → Build → Test → Docker Build → Verify и затем `Finished: SUCCESS`.

Если Docker без permission, на отдельной тестовой машине используй Docker-доступ доверенного пользователя Jenkins или специально подготовленный build-agent; не раскрывай Docker socket внешним недоверенным агентам.

## 7. Jenkins: контролируемое падение и исправление — задание 4.3

Для безопасной демонстрации уже подготовлена отдельная ветка `feature/grigoriev-yanis-ci-failure` — в ней тестовый Pipeline **намеренно** задаёт `DEMO_FAIL_PIPELINE=1` для Gradle, отчего один JUnit тест падает. `main` не затронут.

1. Отправь эту ветку в GitHub: `git push -u origin feature/grigoriev-yanis-ci-failure`.
2. В тестовом Jenkins Job временно переключи Branch Specifier с `*/main` на `*/feature/grigoriev-yanis-ci-failure`.
3. Build Now → убедись, что Test падает; сохрани Console Output с `FAILURE`.
4. Исправь ветку **отдельным коммитом**:

```bash
git switch feature/grigoriev-yanis-ci-failure
sed -i 's/DEMO_FAIL_PIPELINE=1 \.\/gradlew/\.\/gradlew/' Jenkinsfile
git add Jenkinsfile
git commit -m 'Grigoriev Yanis: fix intentional Jenkins test failure'
git push origin feature/grigoriev-yanis-ci-failure
```

5. Build Now → успешная сборка `Finished: SUCCESS`.
6. Верни Branch Specifier на `*/main` для обычного Pipeline и переключись локально на `main`.

На том же тестовом Job можно увидеть failed build и successful build. **Нельзя проводить такой эксперимент на общем production Jenkins.**

## 8. Автоматический запуск после коммита — задание 4.4

В `Jenkinsfile` задано:

```groovy
triggers { pollSCM('H/5 * * * *') }
```

Jenkins периодически проверяет изменения в SCM (примерно каждые 5 минут, с распределением минут по хешу). Для автоматической сборки Jenkins должен оставаться включённым, job должен успешно читать GitHub и настройки SCM/polling не должны блокировать триггер.

Проверка на **main**:

```bash
git switch main
echo 'CI polling test: Grigoriev Yanis' >> README.md
git add README.md
git commit -m 'Grigoriev Yanis: trigger Jenkins via GitHub commit'
git push origin main
```

После очередной проверки репозитория Jenkins должен начать новую сборку **без нажатия Build Now**. В Jenkins → Build History и Console Output должны быть видны коммит, этапы и результат. Вариант с GitHub webhook тоже подходит, но для локального WSL потребуется публично доступный Jenkins endpoint или VPN/туннель с надлежащей защитой.

CI = автоматическая сборка и тесты изменений, CD = автоматизация подготовки к выпуску или доставки; развёртывание в production в задании **не требуется**.

## 9. Какие доказательства сохранить

- Linux: структура папок, лог, анализ, `ls -l` (744), README backup, `ps` и `pgrep`.
- Git: `git log --graph --oneline --all`, ветки, конфликт/его merge commit, URL GitHub, тег.
- Docker: multi-stage Dockerfile, размер образа, Compose services, `docker compose ps`, PostgreSQL healthcheck, persistent record after `down/up`, CRUD HTTP responses.
- Jenkins: Job name, исходный GitHub URL, пять stages, успешный build, failed build и fixed build, история автозапуска после коммита.

**Не выдавать проектные файлы или тестовый вывод за скриншоты реального Jenkins/Docker без их запуска.**
