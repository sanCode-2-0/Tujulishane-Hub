package com.tujulishanehub.backend.models;

public enum CollaboratorRole {
    VIEWER,      // Can only view project details
    EDITOR,      // Can edit project details
    CO_OWNER     // Can edit and manage collaborators
}