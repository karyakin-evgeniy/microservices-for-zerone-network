# microservices-for-zerone-network
Этот проект содержит 5 микросервиса.
1. Eureka-server - сервис для обнаружения всех микросервисов
2. Config-server - сервис для настроек eureka
2. Api-gateway - сервис для связи фронтенда с конечными микросервисами (reports, backend)
3. Reports - сервис для обработки жалоб на контент (отправка, получение). Жалобы сохраняются в базе данных Redis
4. Backend - сервис для работы с самой социальной сетью. Реализована авторизация, работа с постами, друзьями, диалогами, файловой системой, сокеты; подключены модули swagger (для работы без фронта), grafana (статистика, логи), sonarqube. Базы данные: mySQL, Redis, PostgreSQL

Для запуска проекта требуется:
1. Запустить докер 
2. Через командную строку в этой директории выполнить команду docker-compose up
3. Запустить все 5 сервисов![Структура много сервисного приложения](https://user-images.githubusercontent.com/84029079/192139164-0d915bc3-1ab3-453f-b1be-1d32bcd911a1.jpg)

