# DBMS

数据库课程作业，使用 java 实现的一个 DBMS

## 使用示例

### 用户登录及退出

- 用户登录

   ```bash
   语句格式: login username password;
   例句: login root 123;
   ```

- 用户退出

   ```bash
   语法格式: exit;
   ```

### 选择数据库

- 选择数据库

   ```bash
   语法格式: use database_name;
   例句: use db1;
   ```

### 创建操作
  
- 创建数据库

   ```bash
   语法格式: create database database_name;
   例句: create database db2;
   ```

- 创建用户

   ```bash
   语法格式: create user username password;
   例句: create user user1 123;
   ```

- 创建基本表

   ```bash
   语法格式: create table table_name (属性名 属性类型 约束条件);
   例句: create table student (sno varchar, name varchar, score int);
   ```

- 创建视图

   ```bash
   语法格式: create view view_name as select 属性名1, 属性名2…from table table_name;
   例句: create view vstudent as select name , grade from student;
   ```

### 删除操作

- 删除数据库

   ```bash
   语法格式: drop database database_name;
   例句: drop database db2;
   ```

- 删除用户

   ```bash
   语法格式: drop user user_name;
   例句: drop user user1;
   ```

- 删除基本表

   ```bash
   语法格式: drop table table_name;
   例句: drop table t1;
   ```

- 删除视图

   ```bash
   语法格式: drop view view_name;
   例句: drop view v_student;
   ```

### 插入数据

   ```bash
   语法格式: insert into table_name values(value1, value2, value3);
   例句: insert into student  values(“61428”, “wsl”, 88);
   ```

### 删除数据

- 不带where子句

   ```bash
   语法格式: delete from table_name;
   例句: delete from student;
   ```

- 带有where子句

   ```bash
   语法格式: delete from table_name where 条件1 (and || or) 条件2 …;
   例句: delete from student where name != ”wsl” and  name !=”hp”;
   ```

### 更新数据

- 不带where子句

   ```bash
   语法格式: update table_name set 属性1 = 值1, 属性2 = 值2…;
   例句: upodate student set score = 90;
   ```

- 带有where子句

   ```bash
   语法格式: update table_name set 属性1 = 值1, 属性2 = 值2… where 条件1 (and || or) 条件2 …;
   例句: update student set name = "zs", score = 95 where name = "wsl";
   ```

### 查找数据

- 查看所有的数据

   ```bash
   语法格式: select * from table_name;
   例句: select * from student;
   ```

- 查看符合where子句条件的数据

   ```bash
   语法格式: select * from table_name where 条件1(and || or) 条件2…;
   例句: select * from student where name = "ls" or name = "zl";
   ```

- 对结果集排序

   ```bash
   语法格式: select * from table_name [where 条件1(and || or) 条件2…] order by 属性1 (asc || desc);
   例句: select * from student where score > 60 order by sno asc;
   ```

- 聚集函数的使用

   ```bash
   语法格式: select count||avg||sum||min||max (属性名) from table_name where 条件1(and || or) 条件2…;
   例句1: select sum(score) from student;
   例句2: select avg(score) from student;
   例句3: select max(score) from student;
   例句4: select min(score) from student;
   例句5: select count(score) from student;
   ```

- 查询指定列的数据

   ```bash
   语法格式: select属性名1，属性2… from table_name (where 条件1(and || or) 条件2…);
   例句: select sno, name from student where score > 60;
   ```

### 权限管理

- 授予权限

   ```bash
   语法格式: grant (all privileges) || (select, delete …) on table t1, t2 … to user1, user2 …;
   例句1: grant select , delete , update on table student , t1 to user1, user2;
   例句2: grant all privileges on table t1 to u1;
   ```

- 收回权限

   ```bash
   语法格式: revoke (all privileges) || (select, delete …) on table t1, t2 … from user1, user2 …;
   例句1: revoke delete , update on table student from user1;
   例句2: revoke all privileges on table t1 from u1;
   ```

### HELP操作

- 输出当前数据库下所有的表和视图的信息

   ```bash
   语法格式: help database;
   例句: help database;
   ```

- 输出指定基本表的详细信息

   ```bash
   语法格式: help table table_name;
   例句: help table student;
   ```

- 输出指定视图的详细信息

   ```bash
   语法格式: help view view_name;
   例句: help view v_student;
   ```
