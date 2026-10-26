package com.swp391.g1.service;

import com.swp391.g1.model.ActivityType;
import java.util.List;

public interface IActivityTypeService {

    // Get all activity types (for admin)
    List<ActivityType> getAllActivityTypes();
    // Get active activity types (for dropdowns)
    List<ActivityType> getActiveActivityTypes();
    // Get activity type by ID
    ActivityType getActivityTypeById(int id);
    // Create new activity type from Entity
    boolean createActivityType(ActivityType activityType) throws Exception;
    // Update activity type from Entity
    boolean updateActivityType(ActivityType activityType) throws Exception;
    // Delete activity type by ID
    boolean deleteActivityType(int id);
}