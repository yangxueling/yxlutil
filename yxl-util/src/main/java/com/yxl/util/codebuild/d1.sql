/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2012/7/27 星期五 9:50:52                        */
/*==============================================================*/

drop DATABASE yntexam;
CREATE DATABASE yntexam;
USE yntexam;

drop table if exists answer;

drop table if exists exam_plan;

drop table if exists exam_question;

drop table if exists examination;

drop table if exists examination_question_type;

drop table if exists knowledge;

drop table if exists question_bank;

drop table if exists question_result;

drop table if exists score;

drop table if exists structure;

drop table if exists structureType;

drop table if exists student_consult;

drop table if exists subject;

drop table if exists topic_type;

drop table if exists classStructure;

drop table if exists reviews;

/*==============================================================*/
/* Table: answer                                                */
/*==============================================================*/
CREATE TABLE `answer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `reality_score` double DEFAULT NULL COMMENT '实际得分',
  `student_id` int(11) DEFAULT NULL COMMENT '考生id',
  `context` varchar(500) DEFAULT NULL COMMENT '答案内容',
  `question_result_id` int(11) DEFAULT NULL COMMENT '答案id',
  `exam_question_id` int(11) DEFAULT NULL COMMENT '试卷题目ID',
  `question_id` int(11) DEFAULT NULL COMMENT '题目ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='答题表';


/*==============================================================*/
/* Table: exam_plan                                             */
/*==============================================================*/
create table exam_plan(
   id                   int not null auto_increment comment 'ID',
   plan_name            varchar(100) comment '考试安排名称',
   exam_time            int comment '考试时间',
   isSub_time           int comment '开考多久后可交卷时间',
   fraction             double comment '试卷分数',
   pass_fraction        double comment '通过分数',
   teacher_id           int comment '老师id',
   state                int comment '未开考 ，考试中 ，考试暂停，考试完成',
   class_id             int comment '班级id',
   examination_id       int comment '试卷ID',
   start_time           timestamp comment '开考时间',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '考试安排表';

/*==============================================================*/
/* Table: exam_question                                         */
/*==============================================================*/
create table exam_question(
   id                   int not null auto_increment comment 'ID',
   scroe                double comment '试题分数',
   examination_question_type_id int comment '试卷题目类型id',
   `order`              int comment '标识',
   question_bank_id     int comment '题目ID',
   info                 varchar(300) comment '题目解析',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '试卷题目表';

/*==============================================================*/
/* Table: examination                                           */
/*==============================================================*/
create table examination(
   id                   int not null auto_increment comment 'ID',
   examination_name     varchar(50),
   type                 int comment '试卷类型   (1：在线考试    2：在线练习)',
   topic_way            int comment '出题方式(1：随机出题  2：手工出题)',
   is_share             int comment '是否共用  (0：不共用   1：共用)',
   fraction             double comment '卷面总分',
   operate_time         timestamp comment '操作时间',
   teacher_id           int comment '老师id',
   class_id             int comment '班级ID',
   subject_id           int comment '科目ID',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '试卷表';

/*==============================================================*/
/* Table: examination_question_type                             */
/*==============================================================*/
create table examination_question_type(
   id                   int not null auto_increment comment 'id',
   `order`              int comment '序号',
   examination_id       int comment '试卷ID',
   topic_type_id        int comment '题型ID',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '试卷题目类型';

/*==============================================================*/
/* Table: knowledge                                             */
/*==============================================================*/
create table knowledge(
   id                   int not null auto_increment,
   knowledge_name       varchar(30) comment '知识点名称',
   teacher_id           int comment '老师id',
   class_id             int comment '班级ID',
   subject_id           int comment '科目ID',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '知识点表';

/*==============================================================*/
/* Table: question_bank                                         */
/*==============================================================*/
create table question_bank(
   id                   int not null auto_increment,
   question_name        varchar(300) comment '题目名称',
   is_share             int comment '是否共用(0:不共用  1共用)',
   state                int comment '状态(0：可用  1：不可用)',
   operate_time         timestamp comment '日期',
   teacher_id           int comment '老师id',
   question_score       double comment '题目分数',
   question_id          int comment '父ID',
   class_id             int comment '班级ID',
   knowledge_id         int comment '知识点ID',
   topic_type_id        int comment '题目ID', 
   info                 varchar(500) comment '题目分析',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '题库表';

/*==============================================================*/
/* Table: question_result                                       */
/*==============================================================*/
create table question_result(
   id                   int not null auto_increment comment 'id',
   `order`              int comment '答案标识',
   `option`             varchar(30) comment '答案选项',
   context              varchar(200) comment '答案内容',
   isYes                int comment '是否正确答案',
   question_bank_id     int comment '题目ID',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '答案表';

/*==============================================================*/
/* Table: score                                                 */
/*==============================================================*/
create table score(
   id                   int not null auto_increment comment 'ID',
   exam_score           double comment '考试得分',
   subjoin_score        double comment '附加分',
   state                int comment '状态  (1：未阅卷  2：正在阅卷  3：已阅卷)',
   teacher_id           int comment '阅卷老师ID   读取croot中的老师ID',
   student_consult_id   int comment '参考记录表ID',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '成绩表';

/*==============================================================*/
/* Table: structure                                             */
/*==============================================================*/
create table structure(
   id                   int not null auto_increment,
   name                 varchar(30) comment '名称',
   structureType_id     int comment '组织结构类别ID',
   parent_id            int comment '父ID',
   gl_id                int comment '关联ID',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '组织结构表';

/*==============================================================*/
/* Table: structureType                                         */
/*==============================================================*/
create table structureType(
   id                   int not null auto_increment,
   name                 varchar(30) comment '名称',
   type                 int comment '标识',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '组织结构类别表';

/*==============================================================*/
/* Table: student_consult                                       */
/*==============================================================*/
create table student_consult(
   id                   int not null auto_increment comment 'id',
   student_id           int comment '考生id',
   plan_id              int comment '考试安排id',
   state                int comment '状态   考试中   已交卷    已评分',
   time                 timestamp comment '参考时间',
   ip                   varchar(30) comment '考试电脑ip',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '考生参考记录';

/*==============================================================*/
/* Table: subject                                               */
/*==============================================================*/
create table subject(
   id                   int not null auto_increment,
   subject_name         varchar(30) comment '科目名称',
   structure_id         int comment '组织结构ID',
   isDel                int comment '是否删除',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '科目表';

/*==============================================================*/
/* Table: topic_type                                            */
/*==============================================================*/
create table topic_type(
   id                   int not null auto_increment,
   topic_name           varchar(30) comment '题型名称',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '题型表';

/*==============================================================*/
/* Table: classStructure                                         */
/*==============================================================*/
create table classStructure(
   id                   int not null auto_increment,
   class_id             int comment '班级ID',
   structure_id         int comment '组织结构ID',
   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '班级关联表';
/*==============================================================*/
/* Table: Reviews                                               */
/*==============================================================*/
create table reviews
(
   id                   int not null auto_increment comment 'id',
   reviews_context      varchar(500) comment '点评内容',
   student_id           int comment '考生id',
   exam_question_id     int comment '试卷题目ID',
   primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8  comment '点评表';
