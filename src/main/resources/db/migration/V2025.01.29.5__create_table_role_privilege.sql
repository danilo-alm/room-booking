CREATE TABLE Role_Privilege
(
    RoleId          BIGINT UNSIGNED,
    PrivilegeId     BIGINT UNSIGNED,
    PRIMARY KEY (RoleId, PrivilegeId),
    CONSTRAINT FK_RolePrivilege_Role FOREIGN KEY (RoleId) REFERENCES Role(Id),
    CONSTRAINT FK_RolePrivilege_Privilege FOREIGN KEY (PrivilegeId) REFERENCES Privilege(Id)
)