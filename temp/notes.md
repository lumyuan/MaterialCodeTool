```roomsql
create table if not exists note_table(
    id integer primary key AUTOINCREMENT,
    title text,
    content text,
    language text,
    rank integer,
    createDate timestemp,
    classify integer
);
```