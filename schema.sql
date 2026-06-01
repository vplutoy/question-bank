-- 创建数据库
CREATE DATABASE IF NOT EXISTS question_bank DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE question_bank;

-- 教师表
CREATE TABLE IF NOT EXISTS teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    department VARCHAR(100) COMMENT '院系',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL COMMENT '创建教师ID',
    type VARCHAR(30) NOT NULL COMMENT '题型: SINGLE_CHOICE/MULTIPLE_CHOICE/FILL_BLANK/SUBJECTIVE',
    title TEXT NOT NULL COMMENT '题目标题',
    content_json JSON NOT NULL COMMENT '题型专用数据',
    difficulty VARCHAR(10) NOT NULL COMMENT '难度: EASY/MEDIUM/HARD',
    chapter VARCHAR(100) COMMENT '所属章节',
    knowledge_points VARCHAR(500) COMMENT '知识点(逗号分隔)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_teacher (teacher_id),
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 附件表
CREATE TABLE IF NOT EXISTS question_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT COMMENT '关联题目ID',
    file_name VARCHAR(255) COMMENT '原始文件名',
    file_path VARCHAR(500) COMMENT '服务器存储路径',
    file_type VARCHAR(20) DEFAULT 'QUESTION_IMAGE' COMMENT 'QUESTION_IMAGE/ANSWER_IMAGE',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';

-- 种子数据：教师
INSERT INTO teacher (name, department) VALUES
('欧毓毅', '计算机学院'),
('张老师', '计算机学院'),
('李老师', '计算机学院');
