#����һ��state�ı�ֻ��2���ֶ�
create table state(
  id number(2) primary key,
  state number(2)
);
#����������¼
insert into state values(1,0);
insert into state values(2,0);
#��ѯ�����еı�ļ�¼
select * from state;
#�ύ����
commit;