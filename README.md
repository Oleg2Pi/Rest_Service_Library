# Задача:

### Сделать rest сервис на тему: Библиотека

##### Использовать: 

* **servlets**
* **jdbc**

##### Должно быть:

* реализована связь many to many, one to many (3 таблицы )
* unit тесты (покрытие 80%)

##### Нельзя использовать:

* **spring** 
* **hibernate**

### 3 таблицы и связи:

1. **Библиотека (Library)**
    * _library_id_
    * _library_name_
2. **Книги (Books)**
    * _book_id_
    * _title_
    * _author_
    * _library_id_
3. **Читатели (Readers)**
    * _reader_id_
    * _reader_name_

* **Library** : **Books** (1:M)
* **Readers** : **Books** (M:N)
* **Library** : **Readers** (1:M)

##### Для связи многие ко многим будет реализована таблица book_lending c атрибутами:

* _reader_id_
* _book_id_ 
