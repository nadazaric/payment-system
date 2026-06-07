package com.sep.psp.back.feature_superadmin.service.interf;

import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginRequest;
import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginResponse;
import com.sep.psp.back.feature_superadmin.dto.SuperAdminPluginResponse;
import com.sep.psp.back.feature_superadmin.dto.UpdatePluginStatusRequest;

import java.util.List;

public interface SuperAdminService {

    CreateExpectedPluginResponse createExpectedPlugin(CreateExpectedPluginRequest request);

    List<SuperAdminPluginResponse> getPlugins();

    SuperAdminPluginResponse updatePluginStatus(UpdatePluginStatusRequest request);

}