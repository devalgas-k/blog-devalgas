package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.AppInfo;
import com.devalgas.blog.repository.AppInfoRepository;
import com.devalgas.blog.service.AppInfoService;
import com.devalgas.blog.service.dto.AppInfoDTO;
import com.devalgas.blog.service.mapper.AppInfoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.AppInfo}.
 */
@Service
@Transactional
public class AppInfoServiceImpl implements AppInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(AppInfoServiceImpl.class);

    private final AppInfoRepository appInfoRepository;

    private final AppInfoMapper appInfoMapper;

    public AppInfoServiceImpl(AppInfoRepository appInfoRepository, AppInfoMapper appInfoMapper) {
        this.appInfoRepository = appInfoRepository;
        this.appInfoMapper = appInfoMapper;
    }

    @Override
    public AppInfoDTO save(AppInfoDTO appInfoDTO) {
        LOG.debug("Request to save AppInfo : {}", appInfoDTO);
        AppInfo appInfo = appInfoMapper.toEntity(appInfoDTO);
        appInfo = appInfoRepository.save(appInfo);
        return appInfoMapper.toDto(appInfo);
    }

    @Override
    public AppInfoDTO update(AppInfoDTO appInfoDTO) {
        LOG.debug("Request to update AppInfo : {}", appInfoDTO);
        AppInfo appInfo = appInfoMapper.toEntity(appInfoDTO);
        appInfo = appInfoRepository.save(appInfo);
        return appInfoMapper.toDto(appInfo);
    }

    @Override
    public Optional<AppInfoDTO> partialUpdate(AppInfoDTO appInfoDTO) {
        LOG.debug("Request to partially update AppInfo : {}", appInfoDTO);

        return appInfoRepository
            .findById(appInfoDTO.getId())
            .map(existingAppInfo -> {
                appInfoMapper.partialUpdate(existingAppInfo, appInfoDTO);

                return existingAppInfo;
            })
            .map(appInfoRepository::save)
            .map(appInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppInfoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppInfos");
        return appInfoRepository.findAll(pageable).map(appInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppInfoDTO> findOne(Long id) {
        LOG.debug("Request to get AppInfo : {}", id);
        return appInfoRepository.findById(id).map(appInfoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AppInfo : {}", id);
        appInfoRepository.deleteById(id);
    }
}
