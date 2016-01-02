#创建一个state的表，只有2个字段
create table state(
  id number(2) primary key,
  state number(2)
);
#插入两条记录
insert into state values(1,0);
insert into state values(2,0);
#查询出所有的表的记录
select * from state;
#提交数据
commit;