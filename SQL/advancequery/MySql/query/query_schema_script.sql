SET storage_engine=InnoDB;
SET FOREIGN_KEY_CHECKS=0;

drop table if exists COMMONS_GRAPH;
drop table if exists COMMONS_GRAPH_EDGE;
drop table if exists COMMONS_GRAPH_TO_EDGES;
drop table if exists COMMONS_GRAPH_TO_VERTICES;
drop table if exists QUERY;
drop table if exists QUERY_ABSTRACT_QUERY;
drop table if exists QUERY_ARITHMETIC_OPERAND;
drop table if exists QUERY_BASEEXPR_TO_CONNECTORS;
drop table if exists QUERY_BASE_EXPRESSION;
drop table if exists QUERY_BASE_EXPR_OPND;
drop table if exists QUERY_COMPOSITE_QUERY;
drop table if exists QUERY_CONDITION;
drop table if exists QUERY_CONDITION_VALUES;
drop table if exists QUERY_CONNECTOR;
drop table if exists QUERY_CONSTRAINTS;
drop table if exists QUERY_CONSTRAINT_TO_EXPR;
drop table if exists QUERY_COUNT_VIEW;
drop table if exists QUERY_CUSTOM_FORMULA;
drop table if exists QUERY_DATA_VIEW;
drop table if exists QUERY_EXPRESSION;
drop table if exists QUERY_FORMULA_RHS;
drop table if exists QUERY_INTERSECTION;
drop table if exists QUERY_INTER_MODEL_ASSOCIATION;
drop table if exists QUERY_INTRA_MODEL_ASSOCIATION;
drop table if exists QUERY_JOIN_GRAPH;
drop table if exists QUERY_MINUS;
drop table if exists QUERY_MODEL_ASSOCIATION;
drop table if exists QUERY_OPERAND;
drop table if exists QUERY_OPERATION;
drop table if exists QUERY_OUTPUT_ATTRIBUTE;
drop table if exists QUERY_OUTPUT_TERM;
drop table if exists QUERY_PARAMETER;
drop table if exists QUERY_PARAMETERIZED_QUERY;
drop table if exists QUERY_QUERY_ENTITY;
drop table if exists QUERY_RESULT_VIEW;
drop table if exists QUERY_RULE_COND;
drop table if exists QUERY_SUBEXPR_OPERAND;
drop table if exists QUERY_TO_OUTPUT_TERMS;
drop table if exists QUERY_TO_PARAMETERS;
drop table if exists QUERY_UNION;
drop table if exists QUERY_WORKFLOW;
drop table if exists QUERY_WORKFLOW_ITEM;
create table COMMONS_GRAPH (IDENTIFIER bigint not null auto_increment, primary key (IDENTIFIER));
create table COMMONS_GRAPH_EDGE (IDENTIFIER bigint not null auto_increment, SOURCE_VERTEX_CLASS varchar(255), SOURCE_VERTEX_ID bigint, TARGET_VERTEX_CLASS varchar(255), TARGET_VERTEX_ID bigint, EDGE_CLASS varchar(255), EDGE_ID bigint, primary key (IDENTIFIER));
create table COMMONS_GRAPH_TO_EDGES (GRAPH_ID bigint not null, EDGE_ID bigint not null unique, primary key (GRAPH_ID, EDGE_ID));
create table COMMONS_GRAPH_TO_VERTICES (GRAPH_ID bigint not null, VERTEX_CLASS varchar(255), VERTEX_ID bigint);
create table QUERY (IDENTIFIER bigint not null, CONSTRAINTS_ID bigint unique, primary key (IDENTIFIER));
create table QUERY_ABSTRACT_QUERY (IDENTIFIER bigint not null auto_increment, QUERY_NAME varchar(255) unique, DESCRIPTION text, primary key (IDENTIFIER));
create table QUERY_ARITHMETIC_OPERAND (IDENTIFIER bigint not null, LITERAL varchar(255), TERM_TYPE varchar(255), DATE_LITERAL date, TIME_INTERVAL varchar(255), DE_ATTRIBUTE_ID bigint, EXPRESSION_ID bigint, primary key (IDENTIFIER));
create table QUERY_BASEEXPR_TO_CONNECTORS (BASE_EXPRESSION_ID bigint not null, CONNECTOR_ID bigint not null, POSITION integer not null, primary key (BASE_EXPRESSION_ID, POSITION));
create table QUERY_BASE_EXPRESSION (IDENTIFIER bigint not null auto_increment, EXPR_TYPE varchar(255) not null, primary key (IDENTIFIER));
create table QUERY_BASE_EXPR_OPND (BASE_EXPRESSION_ID bigint not null, OPERAND_ID bigint not null, POSITION integer not null, primary key (BASE_EXPRESSION_ID, POSITION));
create table QUERY_COMPOSITE_QUERY (IDENTIFIER bigint not null, OPERATION_ID bigint, primary key (IDENTIFIER));
create table QUERY_CONDITION (IDENTIFIER bigint not null auto_increment, ATTRIBUTE_ID bigint not null, RELATIONAL_OPERATOR varchar(255), primary key (IDENTIFIER));
create table QUERY_CONDITION_VALUES (CONDITION_ID bigint not null, VALUE varchar(255), POSITION integer not null, primary key (CONDITION_ID, POSITION));
create table QUERY_CONNECTOR (IDENTIFIER bigint not null auto_increment, OPERATOR varchar(255), NESTING_NUMBER integer, primary key (IDENTIFIER));
create table QUERY_CONSTRAINTS (IDENTIFIER bigint not null auto_increment, QUERY_JOIN_GRAPH_ID bigint unique, primary key (IDENTIFIER));
create table QUERY_CONSTRAINT_TO_EXPR (CONSTRAINT_ID bigint not null, EXPRESSION_ID bigint not null unique, primary key (CONSTRAINT_ID, EXPRESSION_ID));
create table QUERY_COUNT_VIEW (IDENTIFIER bigint not null, COUNT_ENTITY_ID bigint, primary key (IDENTIFIER));
create table QUERY_CUSTOM_FORMULA (IDENTIFIER bigint not null, OPERATOR varchar(255), LHS_TERM_ID bigint, primary key (IDENTIFIER));
create table QUERY_DATA_VIEW (IDENTIFIER bigint not null, primary key (IDENTIFIER));
create table QUERY_EXPRESSION (IDENTIFIER bigint not null, IS_IN_VIEW BOOLEAN, IS_VISIBLE BOOLEAN, UI_EXPR_ID integer, QUERY_ENTITY_ID bigint, primary key (IDENTIFIER));
create table QUERY_FORMULA_RHS (CUSTOM_FORMULA_ID bigint not null, RHS_TERM_ID bigint not null, POSITION integer not null, primary key (CUSTOM_FORMULA_ID, POSITION));
create table QUERY_INTERSECTION (IDENTIFIER bigint not null, primary key (IDENTIFIER));
create table QUERY_INTER_MODEL_ASSOCIATION (IDENTIFIER bigint not null, SOURCE_SERVICE_URL varchar(255) not null, TARGET_SERVICE_URL varchar(255) not null, SOURCE_ATTRIBUTE_ID bigint not null, TARGET_ATTRIBUTE_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_INTRA_MODEL_ASSOCIATION (IDENTIFIER bigint not null, DE_ASSOCIATION_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_JOIN_GRAPH (IDENTIFIER bigint not null auto_increment, COMMONS_GRAPH_ID bigint, primary key (IDENTIFIER));
create table QUERY_MINUS (IDENTIFIER bigint not null, primary key (IDENTIFIER));
create table QUERY_MODEL_ASSOCIATION (IDENTIFIER bigint not null auto_increment, primary key (IDENTIFIER));
create table QUERY_OPERAND (IDENTIFIER bigint not null auto_increment, OPND_TYPE varchar(255) not null, primary key (IDENTIFIER));
create table QUERY_OPERATION (IDENTIFIER bigint not null auto_increment, OPERAND_ONE bigint, OPERAND_TWO bigint, primary key (IDENTIFIER));
create table QUERY_OUTPUT_ATTRIBUTE (IDENTIFIER bigint not null auto_increment, EXPRESSION_ID bigint, ATTRIBUTE_ID bigint not null, PARAMETERIZED_QUERY_ID bigint, POSITION integer, DATA_VIEW_ID bigint, primary key (IDENTIFIER));
create table QUERY_OUTPUT_TERM (IDENTIFIER bigint not null auto_increment, NAME varchar(255), TIME_INTERVAL varchar(255), TERM_ID bigint, primary key (IDENTIFIER));
create table QUERY_PARAMETER (IDENTIFIER bigint not null auto_increment, NAME varchar(255), OBJECT_CLASS varchar(255), OBJECT_ID bigint, primary key (IDENTIFIER));
create table QUERY_PARAMETERIZED_QUERY (IDENTIFIER bigint not null, primary key (IDENTIFIER));
create table QUERY_QUERY_ENTITY (IDENTIFIER bigint not null auto_increment, ENTITY_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_RESULT_VIEW (IDENTIFIER bigint not null auto_increment, primary key (IDENTIFIER));
create table QUERY_RULE_COND (RULE_ID bigint not null, CONDITION_ID bigint not null, POSITION integer not null, primary key (RULE_ID, POSITION));
create table QUERY_SUBEXPR_OPERAND (IDENTIFIER bigint not null, EXPRESSION_ID bigint, primary key (IDENTIFIER));
create table QUERY_TO_OUTPUT_TERMS (QUERY_ID bigint not null, OUTPUT_TERM_ID bigint not null unique, POSITION integer not null, primary key (QUERY_ID, POSITION));
create table QUERY_TO_PARAMETERS (QUERY_ID bigint not null, PARAMETER_ID bigint not null unique, POSITION integer not null, primary key (QUERY_ID, POSITION));
create table QUERY_UNION (IDENTIFIER bigint not null, primary key (IDENTIFIER));
create table QUERY_WORKFLOW (IDENTIFIER bigint not null auto_increment, USER_ID bigint, QUERY_NAME text, CREATED_ON date, primary key (IDENTIFIER));
create table QUERY_WORKFLOW_ITEM (IDENTIFIER bigint not null auto_increment, QUERY_ID bigint, WORKFLOW_ID bigint, POSITION integer, primary key (IDENTIFIER));
alter table COMMONS_GRAPH_TO_EDGES add index FKA6B0D8BAA0494B1D (GRAPH_ID), add constraint FKA6B0D8BAA0494B1D foreign key (GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table COMMONS_GRAPH_TO_EDGES add index FKA6B0D8BAFAEF80D (EDGE_ID), add constraint FKA6B0D8BAFAEF80D foreign key (EDGE_ID) references COMMONS_GRAPH_EDGE (IDENTIFIER);
alter table COMMONS_GRAPH_TO_VERTICES add index FK2C4412F5A0494B1D (GRAPH_ID), add constraint FK2C4412F5A0494B1D foreign key (GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table QUERY add index FK49D20A886AD86FC (IDENTIFIER), add constraint FK49D20A886AD86FC foreign key (IDENTIFIER) references QUERY_ABSTRACT_QUERY (IDENTIFIER);
alter table QUERY add index FK49D20A89E2FD9C7 (CONSTRAINTS_ID), add constraint FK49D20A89E2FD9C7 foreign key (CONSTRAINTS_ID) references QUERY_CONSTRAINTS (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0BE92C814D (EXPRESSION_ID), add constraint FK262AEB0BE92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0BD635BD31 (IDENTIFIER), add constraint FK262AEB0BD635BD31 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0B96C7CE5A (IDENTIFIER), add constraint FK262AEB0B96C7CE5A foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0BD006BE44 (IDENTIFIER), add constraint FK262AEB0BD006BE44 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0B7223B197 (IDENTIFIER), add constraint FK262AEB0B7223B197 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add index FK262AEB0B687BE69E (IDENTIFIER), add constraint FK262AEB0B687BE69E foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_BASEEXPR_TO_CONNECTORS add index FK3F0043482FCE1DA7 (CONNECTOR_ID), add constraint FK3F0043482FCE1DA7 foreign key (CONNECTOR_ID) references QUERY_CONNECTOR (IDENTIFIER);
alter table QUERY_BASEEXPR_TO_CONNECTORS add index FK3F00434848BA6890 (BASE_EXPRESSION_ID), add constraint FK3F00434848BA6890 foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_BASE_EXPR_OPND add index FKAE67EAF0712A4C (OPERAND_ID), add constraint FKAE67EAF0712A4C foreign key (OPERAND_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_BASE_EXPR_OPND add index FKAE67EA48BA6890 (BASE_EXPRESSION_ID), add constraint FKAE67EA48BA6890 foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_COMPOSITE_QUERY add index FKD453833986AD86FC (IDENTIFIER), add constraint FKD453833986AD86FC foreign key (IDENTIFIER) references QUERY_ABSTRACT_QUERY (IDENTIFIER);
alter table QUERY_COMPOSITE_QUERY add index FKD453833932224F67 (OPERATION_ID), add constraint FKD453833932224F67 foreign key (OPERATION_ID) references QUERY_OPERATION (IDENTIFIER);
alter table QUERY_CONDITION_VALUES add index FK9997379D6458C2E7 (CONDITION_ID), add constraint FK9997379D6458C2E7 foreign key (CONDITION_ID) references QUERY_CONDITION (IDENTIFIER);
alter table QUERY_CONSTRAINTS add index FKE364FCFF1C7EBF3B (QUERY_JOIN_GRAPH_ID), add constraint FKE364FCFF1C7EBF3B foreign key (QUERY_JOIN_GRAPH_ID) references QUERY_JOIN_GRAPH (IDENTIFIER);
alter table QUERY_CONSTRAINT_TO_EXPR add index FK2BD705CEE92C814D (EXPRESSION_ID), add constraint FK2BD705CEE92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_CONSTRAINT_TO_EXPR add index FK2BD705CEA0A5F4C0 (CONSTRAINT_ID), add constraint FK2BD705CEA0A5F4C0 foreign key (CONSTRAINT_ID) references QUERY_CONSTRAINTS (IDENTIFIER);
alter table QUERY_COUNT_VIEW add index FK4A5C8BECF17325F (COUNT_ENTITY_ID), add constraint FK4A5C8BECF17325F foreign key (COUNT_ENTITY_ID) references QUERY_QUERY_ENTITY (IDENTIFIER);
alter table QUERY_COUNT_VIEW add index FK4A5C8BEC89DB039E (IDENTIFIER), add constraint FK4A5C8BEC89DB039E foreign key (IDENTIFIER) references QUERY_RESULT_VIEW (IDENTIFIER);
alter table QUERY_CUSTOM_FORMULA add index FK5C0EEAEFBE674D45 (LHS_TERM_ID), add constraint FK5C0EEAEFBE674D45 foreign key (LHS_TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_CUSTOM_FORMULA add index FK5C0EEAEF12D455EB (IDENTIFIER), add constraint FK5C0EEAEF12D455EB foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_DATA_VIEW add index FK2A3EA74389DB039E (IDENTIFIER), add constraint FK2A3EA74389DB039E foreign key (IDENTIFIER) references QUERY_RESULT_VIEW (IDENTIFIER);
alter table QUERY_EXPRESSION add index FK1B473A8F635766D8 (QUERY_ENTITY_ID), add constraint FK1B473A8F635766D8 foreign key (QUERY_ENTITY_ID) references QUERY_QUERY_ENTITY (IDENTIFIER);
alter table QUERY_EXPRESSION add index FK1B473A8F40EB75D4 (IDENTIFIER), add constraint FK1B473A8F40EB75D4 foreign key (IDENTIFIER) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_FORMULA_RHS add index FKAE90F94D9A0B7164 (CUSTOM_FORMULA_ID), add constraint FKAE90F94D9A0B7164 foreign key (CUSTOM_FORMULA_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_FORMULA_RHS add index FKAE90F94D3BC37DCB (RHS_TERM_ID), add constraint FKAE90F94D3BC37DCB foreign key (RHS_TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_INTERSECTION add index FK2C1FACC0E201AD1D (IDENTIFIER), add constraint FK2C1FACC0E201AD1D foreign key (IDENTIFIER) references QUERY_OPERATION (IDENTIFIER);
alter table QUERY_INTER_MODEL_ASSOCIATION add index FKD70658D15F5AB67E (IDENTIFIER), add constraint FKD70658D15F5AB67E foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION (IDENTIFIER);
alter table QUERY_INTRA_MODEL_ASSOCIATION add index FKF1EDBDD35F5AB67E (IDENTIFIER), add constraint FKF1EDBDD35F5AB67E foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION (IDENTIFIER);
alter table QUERY_JOIN_GRAPH add index FK2B41B5D09DBC4D94 (COMMONS_GRAPH_ID), add constraint FK2B41B5D09DBC4D94 foreign key (COMMONS_GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table QUERY_MINUS add index FK7FD7D5F9E201AD1D (IDENTIFIER), add constraint FK7FD7D5F9E201AD1D foreign key (IDENTIFIER) references QUERY_OPERATION (IDENTIFIER);
alter table QUERY_OPERATION add index FKA13E4E70E4553443 (OPERAND_ONE), add constraint FKA13E4E70E4553443 foreign key (OPERAND_ONE) references QUERY_ABSTRACT_QUERY (IDENTIFIER);
alter table QUERY_OPERATION add index FKA13E4E70E4554829 (OPERAND_TWO), add constraint FKA13E4E70E4554829 foreign key (OPERAND_TWO) references QUERY_ABSTRACT_QUERY (IDENTIFIER);
alter table QUERY_OUTPUT_ATTRIBUTE add index FK22C9DB75E92C814D (EXPRESSION_ID), add constraint FK22C9DB75E92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_OUTPUT_ATTRIBUTE add index FK22C9DB75F961BE22 (DATA_VIEW_ID), add constraint FK22C9DB75F961BE22 foreign key (DATA_VIEW_ID) references QUERY_DATA_VIEW (IDENTIFIER);
alter table QUERY_OUTPUT_ATTRIBUTE add index FK22C9DB75604D4BDA (PARAMETERIZED_QUERY_ID), add constraint FK22C9DB75604D4BDA foreign key (PARAMETERIZED_QUERY_ID) references QUERY_PARAMETERIZED_QUERY (IDENTIFIER);
alter table QUERY_OUTPUT_TERM add index FK13C8A3D388C86B0D (TERM_ID), add constraint FK13C8A3D388C86B0D foreign key (TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_PARAMETERIZED_QUERY add index FKA272176B76177EFE (IDENTIFIER), add constraint FKA272176B76177EFE foreign key (IDENTIFIER) references QUERY (IDENTIFIER);
alter table QUERY_RULE_COND add index FKC32D37AE6458C2E7 (CONDITION_ID), add constraint FKC32D37AE6458C2E7 foreign key (CONDITION_ID) references QUERY_CONDITION (IDENTIFIER);
alter table QUERY_RULE_COND add index FKC32D37AE39F0A10D (RULE_ID), add constraint FKC32D37AE39F0A10D foreign key (RULE_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_SUBEXPR_OPERAND add index FK2BF760E8E92C814D (EXPRESSION_ID), add constraint FK2BF760E8E92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_SUBEXPR_OPERAND add index FK2BF760E832E875C8 (IDENTIFIER), add constraint FK2BF760E832E875C8 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_TO_OUTPUT_TERMS add index FK8A70E25691051647 (QUERY_ID), add constraint FK8A70E25691051647 foreign key (QUERY_ID) references QUERY (IDENTIFIER);
alter table QUERY_TO_OUTPUT_TERMS add index FK8A70E2565E5B9430 (OUTPUT_TERM_ID), add constraint FK8A70E2565E5B9430 foreign key (OUTPUT_TERM_ID) references QUERY_OUTPUT_TERM (IDENTIFIER);
alter table QUERY_TO_PARAMETERS add index FK8060DAD739F0A314 (QUERY_ID), add constraint FK8060DAD739F0A314 foreign key (QUERY_ID) references QUERY_PARAMETERIZED_QUERY (IDENTIFIER);
alter table QUERY_TO_PARAMETERS add index FK8060DAD7F84B9027 (PARAMETER_ID), add constraint FK8060DAD7F84B9027 foreign key (PARAMETER_ID) references QUERY_PARAMETER (IDENTIFIER);
alter table QUERY_UNION add index FK804AC458E201AD1D (IDENTIFIER), add constraint FK804AC458E201AD1D foreign key (IDENTIFIER) references QUERY_OPERATION (IDENTIFIER);
alter table QUERY_WORKFLOW_ITEM add index FK63FE103CA19B1E45 (QUERY_ID), add constraint FK63FE103CA19B1E45 foreign key (QUERY_ID) references QUERY_ABSTRACT_QUERY (IDENTIFIER);
alter table QUERY_WORKFLOW_ITEM add index FK63FE103C40018CD (WORKFLOW_ID), add constraint FK63FE103C40018CD foreign key (WORKFLOW_ID) references QUERY_WORKFLOW (IDENTIFIER);

