# java-filmorate

## Endpoints

### Films
- `[GET] /films` – получить список всех фильмов
- `[GET] /films/{id}` – получить фильм по `id`
- `[POST] /films` – создать новый фильм
- `[PUT] /films` – обновить фильм
- `[GET] /films/popular?count={count}` – получить `count` самых популярных фильмов
- `[PUT] /films/{id}/like/{userId}` – поставить лайк фильму с `id` от пользователя с id `userId`
- `[DELETE] /films/{id}/like/{userId}` – удалить лайк у фильма с `id` от пользователя с id `userId`

### Users
- `[GET] /users` – получить список всех пользователей
- `[GET] /users/{id}` – получить пользователя по `id`
- `[POST] /users` – создать нового пользователя
- `[PUT] /users` – обновить пользователя
- `[PUT] /users/{id}/friends/{friendId}` – добавить пользователю `id` в друзья другого пользователя `friendId`
- `[DELETE] /users/{id}/friends/{friendId}` – удалить у пользователя `id` из друзей другого пользователя `friendId`
- `[GET] /users/{id}/friends` – получить список всех друзей пользователя `id`
- `[GET] /users/{id}/friends/common/{otherId}` – получить список общих друзей пользователей с `id` и `otherId`

## Схема базы данных
![](scheme.png)