package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IActivityTypeDAO;
import com.swp391.g1.dao.impl.ActivityTypeDAOImpl;
import com.swp391.g1.model.ActivityType;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.service.IActivityTypeService;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityTypeServiceImpl implements IActivityTypeService {

    private final IActivityTypeDAO activityTypeDAO;

    public ActivityTypeServiceImpl() {
        this.activityTypeDAO = new ActivityTypeDAOImpl();
    }

    // Get all activity types (for admin)
    @Override
    public List<ActivityType> getAllActivityTypes() {
        return activityTypeDAO.findAll();
    }

    // Get active available types
    @Override
    public List<ActivityType> getActiveActivityTypes() {
        return activityTypeDAO.findAll().stream()
                .filter(type -> type.getStatus() == CommonStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    // Get activity type by ID
    @Override
    public ActivityType getActivityTypeById(int id) {
        if (id <= 0) {
            return null;
        }
        return activityTypeDAO.findById(id);
    }

    //Validate and create new activity type
    @Override
    public boolean createActivityType(ActivityType activityType) throws Exception {
        // Call function to validate the business logic of activity type (without checking ID)
        validateActivityType(activityType, false);
        // Set default status to ACTIVE when creating a new activity type
        activityType.setStatus(CommonStatus.ACTIVE);
        // Call DAO to insert the new activity type
        Integer generatedId = activityTypeDAO.insert(activityType);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updateActivityType(ActivityType activityType) throws Exception {
        // Call function to validate the business logic of activity type (including ID check)
        validateActivityType(activityType, true);
        // Call DAO to update the activity type
        return activityTypeDAO.update(activityType);
    }

    @Override
    public boolean deleteActivityType(int id) {
        if (id <= 0) {
            return false;
        }
        return activityTypeDAO.delete(id);
    }

    // Validate the business logic of ActivityType entity
    private void validateActivityType(ActivityType entity, boolean isUpdate) throws Exception {
        if (entity == null) {
            throw new IllegalArgumentException("Loại hoạt động không được để trống.");
        }

        if (isUpdate && entity.getId() <= 0) {
            throw new IllegalArgumentException("ID loại hoạt động không hợp lệ khi cập nhật.");
        }

        if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại hoạt động không được để trống.");
        }

        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại hoạt động không được để trống.");
        }
    }
}
