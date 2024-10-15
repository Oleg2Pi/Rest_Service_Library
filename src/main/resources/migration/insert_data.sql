-- Вставка тестовых данных в таблицу Libraries
INSERT INTO library (library_name) VALUES ('Central Library');
INSERT INTO library (library_name) VALUES ('Community Library');
INSERT INTO library (library_name) VALUES ('University Library');

-- Вставка тестовых данных в таблицу Books
INSERT INTO books (title, author, library_id) VALUES ('Book One', 'Author A', 1);
INSERT INTO books (title, author, library_id) VALUES ('Book Two', 'Author B', 1);
INSERT INTO books (title, author, library_id) VALUES ('Book Three', 'Author C', 2);

-- Вставка тестовых данных в таблицу Readers
INSERT INTO readers (readers_name) VALUES ('Reader One');
INSERT INTO readers (readers_name) VALUES ('Reader Two');
INSERT INTO readers (readers_name) VALUES ('Reader Three');

-- Вставка данных о заимствовании книг
INSERT INTO book_lending (reader_id, book_id) VALUES (1, 1);
INSERT INTO book_lending (reader_id, book_id) VALUES (1, 2);
INSERT INTO book_lending (reader_id, book_id) VALUES (2, 1);
INSERT INTO book_lending (reader_id, book_id) VALUES (3, 3);