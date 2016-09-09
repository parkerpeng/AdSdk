LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := installer.c
LOCAL_MODULE := installer
LOCAL_LDFLAGS := -static
include $(BUILD_EXECUTABLE)
