ʹ��˵��
�û���¼���˳�
	�û���¼
		����ʽ: login username password;
		���䣺login root 123;
	�û��˳�
		�﷨��ʽ��exit;
ѡ�����ݿ�
	�﷨��ʽ: use database_name;
	���䣺use db1;
��������
	�������ݿ�
		�﷨��ʽ��create database database_name; 
		���䣺create database db2;
	�����û�
		�﷨��ʽ��create user username password;
		���䣺create user user1 123;
	����������
		�﷨��ʽ��create table table_name (������ �������� Լ������);
		���䣺create table student (sno varchar, name varchar, score int);
	������ͼ
		�﷨��ʽ��create view view_name as select ������1, ������2��from table table_name;
		���䣺create view vstudent as select name , grade from student;
ɾ������
	ɾ�����ݿ�
		�﷨��ʽ��drop database database_name;
		���䣺drop database db2;
	ɾ���û�
�﷨��ʽ��drop user user_name;
���䣺drop user user1;
	ɾ��������
		�﷨��ʽ��drop table table_name;
		���䣺drop table t1;
	ɾ����ͼ
		�﷨��ʽ��drop view view_name;
		���䣺drop view v_student;
��������
	�﷨��ʽ��insert into table_name values(value1, value2, value3);
	���䣺insert into student  values(��61428��, ��wsl��, 88);
ɾ������
	����where�Ӿ�
		�﷨��ʽ��delete from table_name;
		���䣺delete from student;
	����where�Ӿ�
		�﷨��ʽ��delete from table_name where ����1 (and || or) ����2 ��;
		���䣺delete from student where name != ��wsl�� and  name !=��hp��;
��������
	����where�Ӿ�
		�﷨��ʽ��update table_name set ����1 = ֵ1, ����2 = ֵ2��;
		���䣺upodate student set score = 90;
	����where�Ӿ�
		�﷨��ʽ��update table_name set ����1 = ֵ1, ����2 = ֵ2�� where ����1 (and || or) ����2 ��;
		���䣺update student set name = "zs", score = 95 where name = "wsl";
��������
	�鿴���е�����
		�﷨��ʽ��select * from table_name;
		���䣺select * from student;
	�鿴����where�Ӿ�����������
		�﷨��ʽ��select * from table_name where ����1(and || or) ����2��;
		���䣺select * from student where name = "ls" or name = "zl";
	�Խ��������
		�﷨��ʽ��select * from table_name [where ����1(and || or) ����2��] order by ����1 (asc || desc);
		���䣺select * from student where score > 60 order by sno asc;
	�ۼ�������ʹ��
		�﷨��ʽ��select count||avg||sum||min||max (������) from table_name where ����1(and || or) ����2��;
		����1��select sum(score) from student;
		����2��select avg(score) from student;
		����3��select max(score) from student;
		����4��select min(score) from student;
		����5��select count(score) from student;
	��ѯָ���е�����
		�﷨��ʽ��select������1������2�� from table_name (where ����1(and || or) ����2��);
		���䣺select sno, name from student where score > 60;
Ȩ�޹���
	����Ȩ��
		�﷨��ʽ��grant (all privileges) || (select, delete ��) on table t1, t2 �� to user1, user2 ��;
		����1��grant select , delete , update on table student , t1 to user1, user2;
		����2��grant all privileges on table t1 to u1;
	�ջ�Ȩ��
		�﷨��ʽ��revoke (all privileges) || (select, delete ��) on table t1, t2 �� from user1, user2 ��;
		����1��revoke delete , update on table student from user1;
		����2��revoke all privileges on table t1 from u1;
HELP����
	�����ǰ���ݿ������еı����ͼ����Ϣ
		�﷨��ʽ��help database;
		���䣺help database;
	���ָ�����������ϸ��Ϣ
		�﷨��ʽ��help table table_name;
		���䣺help table student;
	���ָ����ͼ����ϸ��Ϣ
		�﷨��ʽ��help view view_name;
		���䣺help view v_student;
