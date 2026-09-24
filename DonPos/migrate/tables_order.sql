delete from products_lots;
delete from lots;
delete from products_cat;
delete from products;
delete from categories;
delete from customers;
delete from closedcash;
delete from ticketsnum;
delete from ticketsnum_refund;
delete from taxpayer;
delete from establishments;
delete from ticketsnum_purchase;
delete from people;

select * from people;
select * from categories;
update products set uom = 'u';
select * from products;
select * from products_cat;
select * from customers;
select * from closedcash;
select * from receipts;
SET FOREIGN_KEY_CHECKS = 0;
select * from tickets;
SET FOREIGN_KEY_CHECKS = 1;
ALTER TABLE ticketlines MODIFY COLUMN lot varchar(255) DEFAULT '0' NOT NULL;
select * from ticketlines;
ALTER TABLE ticketlines MODIFY COLUMN lot varchar(255) NOT NULL;
select * from payments;
select * from ticketsnum;
select * from ticketsnum_refund;
select * from taxpayer;
--(id,identification,legal_name,text_1,text_2,text_3,text_4) 
select * from establishments;
-- Ambiente de facturación electrónica: Test -> 1; Production -> 2
-- resources '92', 'Electronic.Environment'
select * from ele_documents;
