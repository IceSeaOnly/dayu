package site.binghai.lib.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.SysConfig;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SysConfigService extends BaseService<SysConfig> {
    public boolean isSystemClosed() {
        SysConfig config = findById(1L);
        return config == null ? false : config.getCloseSystem();
    }

    public String getCloseReason() {
        SysConfig config = findById(1L);
        return config == null ? "系统临时关闭" : config.getCloseMessage();
    }

    public SysConfig getConfig(){
        SysConfig sysConfig = findById(1L);
        if(sysConfig == null){
            sysConfig = new SysConfig();
            sysConfig.setCloseSystem(false);
            sysConfig.setCloseMessage("系统临时关闭");
            return save(sysConfig);
        }
        return sysConfig;
    }

    @Transactional
    public void setSystem(SysConfig config) {
        config.setId(1L);
        update(config);
    }
}
