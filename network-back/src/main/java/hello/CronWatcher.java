/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author lionel
 */
@Component
public class CronWatcher implements InitializingBean{

    private static final Logger log = LoggerFactory.getLogger(CronWatcher.class);

    @Autowired
    private ImporterData importer;
    
    
    @PostConstruct
    public void fileWatcher() {
        //importer.hello();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
    }

}
