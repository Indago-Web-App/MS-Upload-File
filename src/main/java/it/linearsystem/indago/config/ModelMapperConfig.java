package it.linearsystem.indago.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author by Andrea Zaccanti
 * @Created 15 Maggio 2020
 * 
 */
@Configuration
public class ModelMapperConfig {
	
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    
//  @Bean
//  public ModelMapper modelMapper() {
//      ModelMapper mm = new ModelMapper();
//      Converter<CluDispClusterEntity, DispClusterDto> dispClusterDtoConverter = mappingContext -> {
//          CluDispClusterEntity dispClusterEntity = mappingContext.getSource();
//          DispClusterDto dispClusterDto = new DispClusterDto();
//
//          dispClusterDto.setId(dispClusterDto.getId());
//          dispClusterDto.setIdCluster(dispClusterDto.getIdCluster());
//          dispClusterDto.setIdDispositivo(dispClusterEntity.getIdDispositivo());
//          return dispClusterDto;
//      };
//      return mm;
//  }
    
//  @Bean
//  public ModelMapper modelMapperOLD() {
//      ModelMapper mm = new ModelMapper();
//      Converter<ConfigurazioniDto, ConConfigurazioniEntity> configurazioniEntityConverter = mappingContext -> {
//          ConfigurazioniDto confDto = mappingContext.getSource();
//          ConConfigurazioniEntity confEntity = new ConConfigurazioniEntity();
//
//          confEntity.setIdConfigurazione(confDto.getIdConfigurazione());
//          confEntity.setDesConfigurazione(confDto.getDesConfigurazione());
//
//          confEntity.setDes4gCfgApn(confDto.getDes4gCfgApn());
//          confEntity.setDes4gStatus(confDto.getDes4gStatus());
//
//          confEntity.setDesAudioStatus(confDto.getDesAudioStatus());
//          confEntity.setDesAudioVolume(confDto.getDesAudioVolume());
//
//          confEntity.setDesBthCfgDevname(confDto.getDesBthCfgDevname());
//          confEntity.setDesBthCfgVisible(confDto.getDesBthCfgVisible());
//          confEntity.setDesBthStatus(confDto.getDesBthStatus());
//
//          confEntity.setDesCameraStatus(confDto.getDesCameraStatus());
//
//          confEntity.setDesExtkbEsckey(confDto.getDesExtkbEsckey());
//          confEntity.setDesExtkbHomekey(confDto.getDesExtkbHomekey());
//          confEntity.setDesExtkbStatus(confDto.getDesExtkbStatus());
//
//          confEntity.setDesGpsStatus(confDto.getDesGpsStatus());
//          confEntity.setDesNfcStatus(confDto.getDesNfcStatus());
//
//          confEntity.setDesWifiCfgPass(confDto.getDesWifiCfgPass());
//          confEntity.setDesWifiCfgSsid(confDto.getDesWifiCfgSsid());
//          confEntity.setDesWifiStatus(confDto.getDesWifiStatus());
//
//          confEntity.setDesNbAltro(confDto.getDesNbAltro());
//          confEntity.setDesNbTime(confDto.getDesNbTime());
//          confEntity.setDesNbBattery(confDto.getDesNbBattery());
//
//          confEntity.setDesNbeAppSwitcher(confDto.getDesNbeAppSwitcher());
//          confEntity.setDesNbeBack(confDto.getDesNbeBack());
//          confEntity.setDesNbeHome(confDto.getDesNbeHome());
//
//          confEntity.setDtAttivazione(confDto.getDtAttivazione());
//          confEntity.setDtCreazione(confDto.getDtCreazione());
//          confEntity.setDtDisattivazione(confDto.getDtDisattivazione());
//          confEntity.setDtDisattivazione(confDto.getDtDisattivazione());
//
//          Set<ConAppConfigurazioniEntity> appConfigurazioniEntities = new HashSet<>();
//          confDto.getApps().forEach(appConfigurazioniDto -> {
//              ConAppConfigurazioniEntity appConfigurazioniEntity = new ConAppConfigurazioniEntity();
//
//              appConfigurazioniEntity.setId(appConfigurazioniDto.getId());
//              appConfigurazioniEntity.setIdConfigurazione(confDto.getIdConfigurazione());
//              appConfigurazioniEntity.setIdApplicazione(appConfigurazioniDto.getIdApplicazione());
//
//              appConfigurazioniEntity.setValOrdineApplicazioni(appConfigurazioniDto.getValOrdineApplicazioni());
//              appConfigurazioniEntity.setValOrdineStrumenti(appConfigurazioniDto.getValOrdineStrumenti());
//              appConfigurazioniEntity.setCodAzione(appConfigurazioniDto.getCodAzione());
//
//              appConfigurazioniEntities.add(appConfigurazioniEntity);
//          });
//
//          confEntity.setApps(appConfigurazioniEntities);
//
//          Set<ConDispConfigurazioniEntity> dispConfigurazioniEntities = new HashSet<>();
//          confDto.getDevices().forEach(dispDto -> {
//              ConDispConfigurazioniEntity dispConfigurazioniEntity = new ConDispConfigurazioniEntity();
//
//              dispConfigurazioniEntity.setIdConfigurazione(confDto.getIdConfigurazione());
//              dispConfigurazioniEntity.setId(dispDto.getId());
//              dispConfigurazioniEntity.setIdDispositivo(dispDto.getIdDispositivo());
//              dispConfigurazioniEntity.setIdCluster(dispDto.getIdCluster());
//          });
//          confEntity.setDevices(dispConfigurazioniEntities);
//
//          Set<ConImpostazioniEntity> impostazioniEntities = new HashSet<>();
//          confDto.getImpostazioni().forEach(dto -> {
//              ConImpostazioniEntity impostazioniEntity = new ConImpostazioniEntity();
//
//              impostazioniEntity.setId(dto.getId());
//              impostazioniEntity.setIdConfigurazione(confDto.getIdConfigurazione());
//              impostazioniEntity.setIdUtente(dto.getIdUtente());
//
//              impostazioniEntity.setCodLinguaIso(dto.getCodLinguaIso());
//              impostazioniEntity.setDesAnimazione(dto.getDesAnimazione());
//              impostazioniEntity.setDesDesktop(dto.getDesDesktop());
//              impostazioniEntity.setValTimeout(dto.getValTimeout());
//
//              impostazioniEntities.add(impostazioniEntity);
//          });
//
//          confEntity.setImpostazioni(impostazioniEntities);
//          return confEntity;
//      };
//      mm.addConverter(configurazioniEntityConverter);
//      return mm;
//  }
  
//  @Bean
//  public ModelMapper modelMapper() {
//      ModelMapper mm = new ModelMapper();
//      Converter<MDMConfiguration, Configurazione> configurazioneConverter = mappingContext -> {
//      	MDMConfiguration mdmConfiguration = mappingContext.getSource();
////          ConConfigurazioniEntity confEntity = new ConConfigurazioniEntity();
//          Configurazione configurazione = new Configurazione();
//
//          List<ApplicazioneConfigurazione> applicazioneConfigurazioneList = new ArrayList<ApplicazioneConfigurazione>();
//          
//          mdmConfiguration.getApps().getInstalledApps().stream()
//          .forEach(elem -> {
//          	ApplicazioneConfigurazione applicazioneConfigurazione = new ApplicazioneConfigurazione();
//          	applicazioneConfigurazione.setApplicazioneId( elem.getId() );
////          	applicazioneConfigurazione.setConfigurazioneId(configurazioneId);
//          	applicazioneConfigurazione.setConfigurazione( configurazione );
//          	applicazioneConfigurazione.setFlagAutostart( elem.isBootStartup());
//          	applicazioneConfigurazione.setFlagPermission( elem.hasDefaultPermission() );
//          	applicazioneConfigurazione.setOperation("I");
//          	applicazioneConfigurazione.setVisible( elem.isKioskVisibility() );
//          	applicazioneConfigurazione.setOrdineApplicazioni( elem.getOrdineApplicazioni() );
//          	applicazioneConfigurazione.setOrdineStrumenti( elem.getOrdineStrumenti() );
//          	
//          	applicazioneConfigurazioneList.add(applicazioneConfigurazione);
//          	
////          	ApplicazioneConfigurazione applicazioneConfigurazioneSaved = applicazioneConfigurazioneRepository.save(applicazioneConfigurazione);
////          	MDMConfiguration configurazioneDto = modelMapper.map(elem, MDMConfiguration.class);
////              log.debug("applicazioneConfigurazioneSaved: {}", applicazioneConfigurazioneSaved);
////              configurazioniList.add(configurazioneDto);
//          });
//          
//          mdmConfiguration.getApps().getUninstalledApps().stream()
//          .forEach(elem -> {
//          	ApplicazioneConfigurazione applicazioneConfigurazione = null;
//          	if (applicazioneConfigurazioneRepository.existsByApplicazioneId( elem.longValue() ) ) {
//          		applicazioneConfigurazione = applicazioneConfigurazioneRepository.findByApplicazioneId( elem.longValue() );
//          	} else {
//          		applicazioneConfigurazione = new ApplicazioneConfigurazione();
//          	}
//          	applicazioneConfigurazione.setApplicazioneId( elem.longValue() );
////          	applicazioneConfigurazione.setConfigurazioneId( null );
//          	applicazioneConfigurazione.setConfigurazione( configurazione );
//          	applicazioneConfigurazione.setFlagAutostart( null );
//          	applicazioneConfigurazione.setFlagPermission( null );
//          	applicazioneConfigurazione.setOperation("D");
//          	applicazioneConfigurazione.setVisible( null );
//          	applicazioneConfigurazione.setOrdineApplicazioni( null );
//          	applicazioneConfigurazione.setOrdineStrumenti( null );
//          	
//          	applicazioneConfigurazioneList.add(applicazioneConfigurazione);
//          	
////          	ApplicazioneConfigurazione applicazioneConfigurazioneSaved = applicazioneConfigurazioneRepository.save(applicazioneConfigurazione);
////          	MDMConfiguration configurazioneDto = modelMapper.map(elem, MDMConfiguration.class);
////              log.debug("applicazioneConfigurazioneSaved: {}", applicazioneConfigurazioneSaved);
////              configurazioniList.add(configurazioneDto);
//          });
//          
//          configurazione.setApplicazioneConfiguraziones(applicazioneConfigurazioneList);
//          
////          mdmConfiguration.getApps().findMd5FromApk(packageName)
//          
////          List<Componenti> componentiList = new ArrayList<Componenti>();
//          Set<Componenti> componentiList = new HashSet<Componenti>();
//          
//          Componenti componenteHW = new Componenti();
//          componenteHW.setAudioStatus( mdmConfiguration.getDeviceComponents().getAudio().isEnabled() );
//          componenteHW.setAudioVolume( Long.valueOf( mdmConfiguration.getDeviceComponents().getAudio().getVolume() ) );
//          
//          componenteHW.setGpsStatus( mdmConfiguration.getDeviceComponents().isGpsEnabled() );
//          componenteHW.setNfcStatus( mdmConfiguration.getDeviceComponents().isNfcEnabled() );
//          
//          componenteHW.setBthCfgDevname( mdmConfiguration.getDeviceComponents().getBluetooth().getDeviceName() );
//          componenteHW.setBthCfgVisible( mdmConfiguration.getDeviceComponents().getBluetooth().isVisible() );
//          componenteHW.setBthStatus( mdmConfiguration.getDeviceComponents().getBluetooth().isEnabled() );
//          
//          componenteHW.setCameraStatus( mdmConfiguration.getDeviceComponents().isCameraEnabled() );
//       
//          // NO - TOGLIERE A DB
////          componenteHW.getExtKeybrdEsckey()
////          componenteHW.setExtKeybrdHomekey(extKeybrdHomekey);
////          componenteHW.setExtKeybrdStatus(extKeybrdStatus);
//          
//          componenteHW.setWifiCfgPass( mdmConfiguration.getDeviceComponents().getWifi().getPassword() );
//          componenteHW.setWifiCfgSsid( mdmConfiguration.getDeviceComponents().getWifi().getSsid() );
//          componenteHW.setWifiStatus( mdmConfiguration.getDeviceComponents().getWifi().isEnabled() );        
//          componentiList.add(componenteHW);
//
//          Componenti componenteSW = new Componenti();
//          
////          componenteSW.set_gCfgApn(_gCfgApn); NO _ DA TOGLIERE A DB
////          componenteSW.set_gStatus(_gStatus); NO 
//
//  		componenteSW.setNavBarBack( mdmConfiguration.getSoftwareComponents().getBackEnabled() /* mdmConfiguration.getSoftwareComponents().getNavigationBar().isBackEnabled()*/ );
//          componenteSW.setNavBarHome( mdmConfiguration.getSoftwareComponents().getHomeEnabled() /*mdmConfiguration.getSoftwareComponents().getNavigationBar().isHomeEnabled()*/ );
//          componenteSW.setNavBarRecent( mdmConfiguration.getSoftwareComponents().getRecentEnabled() /*mdmConfiguration.getSoftwareComponents().getNavigationBar().isRecentEnabled()*/ );
//          componenteSW.setNotificationBar( mdmConfiguration.getSoftwareComponents().isNotificationBarEnabled() );
////        componenteSW.setDesScreensaverSet(desScreensaverSet); DA SOSTITUIRE CON IL TIMEOUT
////        componenteSW.setScreensaverStatus(screensaverStatus); DA INSERIRE CON enabled
//   
////          mdmConfiguration.getSoftwareComponents().getLanguage()
////          mdmConfiguration.getSoftwareComponents().getCountry()
////          mdmConfiguration.getSoftwareComponents().getLocale()
//          
////          mdmConfiguration.getSoftwareComponents().getTimestamp()
////          mdmConfiguration.getSoftwareComponents().getTimezone()        
//          componentiList.add(componenteSW); 
//          
//          configurazione.setComponentis(componentiList);
//          
//          // NO
////          List<Distribuzione> distribuzioni = new ArrayList<Distribuzione>();
////          Distribuzione distribuzione = new Distribuzione();
////          distribuzione.setId(id);
////          distribuzione.setNome(nome);
////          distribuzione.setDataCreazione(dataCreazione);
////          distribuzione.setDataAttivazione(dataAttivazione);
////          distribuzione.setDataAggiornamento(dataAggiornamento);
////          distribuzione.setDataConclusione(dataConclusione);
////          distribuzioni.add(distribuzione);
////          configurazione.setDistribuziones(distribuzioni);
//          
//          // NO
////          List<Security> securities = new ArrayList<Security>();
////          Security security = new Security();
////          security.setId(id);
////          security.setLauncherHashPwd(launcherHashPwd);
////          security.setListPkgBlacklist(listPkgBlacklist);
////          security.setListPkgWhitelist(listPkgWhitelist);
////          security.setConfigurazione(configurazione);
////          securities.add(security);
////          configurazione.setSecurities(securities); // N.A. FE
//          
////          configurazione.setId( mdmConfiguration.getInfo().getConfigurationId().longValue() );
//          configurazione.setNome( mdmConfiguration.getInfo().getConfigurationName() );
//          configurazione.setRelease(  mdmConfiguration.getInfo().getRelease() ); // ARRIVA SOLO DA FE
//          configurazione.setTerminalType( mdmConfiguration.getInfo().getTerminalTypeId().longValue() );
//
//          
//          return configurazione;
//      };
//      mm.addConverter( configurazioneConverter );
//      PropertyMap<MDMConfiguration, Configurazione> propertyMap = new PropertyMap<MDMConfiguration, Configurazione>() {
//      	   protected void configure() {
//      	      using(configurazioneConverter).map(source.getInfo().getConfigurationId()).setId(null);
//      	      using(configurazioneConverter).map(source.getInfo().getConfigurationName()).setNome(null);
//      	      using(configurazioneConverter).map(source.getInfo().getRelease()).setRelease(null);
//      	      using(configurazioneConverter).map(source.getInfo().getTerminalTypeId()).setTerminalType(null);
//      	      using(configurazioneConverter).map(source.getApps()).setApplicazioneConfiguraziones(null);
//      	      using(configurazioneConverter).map(source.getDeviceComponents()).setComponentis(null);
//      	      using(configurazioneConverter).map(source.getSoftwareComponents()).setDistribuziones(null);
//      	      using(configurazioneConverter).map(source.getSoftwareComponents()).setSecurities(null);
////      		   using(configurazioneConverter).map(source.getInfo().getConfigurationId()).setId(null);
//      	   }
//      	};
//      mm.addMappings(propertyMap);
//      mm.validate();
//      return mm;
//  }
  
//  @Bean
//  public ModelMapper modelMapper() {
//      ModelMapper mm = new ModelMapper();
//      Configurazione configurazione = new Configurazione();
//////      Converter<MDMConfiguration, Configurazione> configurazioneConverter = mappingContext -> {
////      Converter<AppsFrontend, Configurazione> configurazioneConverter = mappingContext -> {
//////      	Failed to set value 'Configurazione(id=null, nome=null, release=null, terminalType=null, componentis=null, distribuziones=null, securities=null, applicazioneConfiguraziones=null)' on my.maven.project.configurazioni.entity.Configurazione.setApplicazioneConfiguraziones()
////      	System.out.println( mappingContext.getSource() ); //AppsFrontend
////      	System.out.println( mappingContext.getDestination() ); //null
////      	System.out.println( mappingContext.getParent()); //MappingContext[MDMConfiguration -> Configurazione]
////      	System.out.println( mappingContext.getMapping() ); //PropertyMapping[MDMConfiguration.apps -> Configurazione.applicazioneConfiguraziones]
////      	System.out.println( mappingContext.getTypeMap() ); //null
////      	AppsFrontend appsFrontend = mappingContext.getSource();
////
////        List<ApplicazioneConfigurazione> applicazioneConfigurazioneList = new ArrayList<ApplicazioneConfigurazione>();
////        
////        appsFrontend.getInstalledApps().stream()
////        .forEach(elem -> {
////        	ApplicazioneConfigurazione applicazioneConfigurazione = new ApplicazioneConfigurazione();
////        	applicazioneConfigurazione.setApplicazioneId( elem.getId() );
//////        	applicazioneConfigurazione.setConfigurazioneId(configurazioneId);
////        	applicazioneConfigurazione.setConfigurazione( configurazione );
////        	applicazioneConfigurazione.setFlagAutostart( elem.isBootStartup());
////        	applicazioneConfigurazione.setFlagPermission( elem.hasDefaultPermission() );
////        	applicazioneConfigurazione.setOperation("I");
////        	applicazioneConfigurazione.setVisible( elem.isKioskVisibility() );
////        	applicazioneConfigurazione.setOrdineApplicazioni( elem.getOrdineApplicazioni() );
////        	applicazioneConfigurazione.setOrdineStrumenti( elem.getOrdineStrumenti() );
////        	
////        	applicazioneConfigurazioneList.add(applicazioneConfigurazione);
////        	
//////        	ApplicazioneConfigurazione applicazioneConfigurazioneSaved = applicazioneConfigurazioneRepository.save(applicazioneConfigurazione);
//////        	MDMConfiguration configurazioneDto = modelMapper.map(elem, MDMConfiguration.class);
//////            log.debug("applicazioneConfigurazioneSaved: {}", applicazioneConfigurazioneSaved);
//////            configurazioniList.add(configurazioneDto);
////        });
////        appsFrontend.getUninstalledApps().stream()
////	        .forEach(elem -> {
////	        	ApplicazioneConfigurazione applicazioneConfigurazione = null;
//////	        	if (applicazioneConfigurazioneRepository.existsByApplicazioneId( elem.longValue() ) ) {
//////	        		applicazioneConfigurazione = applicazioneConfigurazioneRepository.findByApplicazioneId( elem.longValue() );
//////	        	} else {
////	        		applicazioneConfigurazione = new ApplicazioneConfigurazione();
//////	        	}
////	        	applicazioneConfigurazione.setApplicazioneId( elem.longValue() );
////	//        	applicazioneConfigurazione.setConfigurazioneId( null );
////	        	applicazioneConfigurazione.setConfigurazione( configurazione );
////	        	applicazioneConfigurazione.setFlagAutostart( null );
////	        	applicazioneConfigurazione.setFlagPermission( null );
////	        	applicazioneConfigurazione.setOperation("D");
////	        	applicazioneConfigurazione.setVisible( null );
////	        	applicazioneConfigurazione.setOrdineApplicazioni( null );
////	        	applicazioneConfigurazione.setOrdineStrumenti( null );
////	        	
////	        	applicazioneConfigurazioneList.add(applicazioneConfigurazione);
////	        	
////	//        	ApplicazioneConfigurazione applicazioneConfigurazioneSaved = applicazioneConfigurazioneRepository.save(applicazioneConfigurazione);
////	//        	MDMConfiguration configurazioneDto = modelMapper.map(elem, MDMConfiguration.class);
////	//            log.debug("applicazioneConfigurazioneSaved: {}", applicazioneConfigurazioneSaved);
////	//            configurazioniList.add(configurazioneDto);
////	        });
////	        
////	        configurazione.setApplicazioneConfiguraziones(applicazioneConfigurazioneList);
////      	
////          return configurazione;
////      };
////      mm.addConverter( configurazioneConverter );
////      
////      Set<Componenti> componentiList = new HashSet<Componenti>();
////      
////      Converter<DeviceComponents, Configurazione> configurazioneConverter2 = mappingContext -> {
//////      	Failed to set value 'Configurazione(id=null, nome=null, release=null, terminalType=null, componentis=null, distribuziones=null, securities=null, applicazioneConfiguraziones=null)' on my.maven.project.configurazioni.entity.Configurazione.setApplicazioneConfiguraziones()
////      	System.out.println( mappingContext.getSource() ); //AppsFrontend
////      	System.out.println( mappingContext.getDestination() ); //null
////      	System.out.println( mappingContext.getParent()); //MappingContext[MDMConfiguration -> Configurazione]
////      	System.out.println( mappingContext.getMapping() ); //PropertyMapping[MDMConfiguration.apps -> Configurazione.applicazioneConfiguraziones]
////      	System.out.println( mappingContext.getTypeMap() ); //null
////      	DeviceComponents deviceComponents = mappingContext.getSource();
////      	
//////      	Set<Componenti> componentiList = new HashSet<Componenti>();
////
////      	Componenti componenteHW = new Componenti();
////      	componenteHW.setAudioStatus( deviceComponents.getAudio().isEnabled() );
////      	componenteHW.setAudioVolume( Long.valueOf( deviceComponents.getAudio().getVolume() ) );
////
////      	componenteHW.setGpsStatus( deviceComponents.isGpsEnabled() );
////      	componenteHW.setNfcStatus( deviceComponents.isNfcEnabled() );
////
////      	componenteHW.setBthCfgDevname( deviceComponents.getBluetooth().getDeviceName() );
////      	componenteHW.setBthCfgVisible( deviceComponents.getBluetooth().isVisible() );
////      	componenteHW.setBthStatus( deviceComponents.getBluetooth().isEnabled() );
////
////      	componenteHW.setCameraStatus( deviceComponents.isCameraEnabled() );
////
////      	// NO - TOGLIERE A DB
////      	//          componenteHW.getExtKeybrdEsckey()
////      	//          componenteHW.setExtKeybrdHomekey(extKeybrdHomekey);
////      	//          componenteHW.setExtKeybrdStatus(extKeybrdStatus);
////
////      	componenteHW.setWifiCfgPass( deviceComponents.getWifi().getPassword() );
////      	componenteHW.setWifiCfgSsid( deviceComponents.getWifi().getSsid() );
////      	componenteHW.setWifiStatus( deviceComponents.getWifi().isEnabled() );        
////      	componentiList.add(componenteHW);
////
////          return configurazione;
////      };
////      mm.addConverter( configurazioneConverter2 );
////      
////      Converter<SoftwareComponents, Configurazione> configurazioneConverter3 = mappingContext -> {
//////      	Failed to set value 'Configurazione(id=null, nome=null, release=null, terminalType=null, componentis=null, distribuziones=null, securities=null, applicazioneConfiguraziones=null)' on my.maven.project.configurazioni.entity.Configurazione.setApplicazioneConfiguraziones()
////      	System.out.println( mappingContext.getSource() ); //AppsFrontend
////      	System.out.println( mappingContext.getDestination() ); //null
////      	System.out.println( mappingContext.getParent()); //MappingContext[MDMConfiguration -> Configurazione]
////      	System.out.println( mappingContext.getMapping() ); //PropertyMapping[MDMConfiguration.apps -> Configurazione.applicazioneConfiguraziones]
////      	System.out.println( mappingContext.getTypeMap() ); //null
////      	SoftwareComponents softwareComponents = mappingContext.getSource();
////      	
////      	Componenti componenteSW = new Componenti();
////
////      	//          componenteSW.set_gCfgApn(_gCfgApn); NO _ DA TOGLIERE A DB
////      	//          componenteSW.set_gStatus(_gStatus); NO 
////
////      	componenteSW.setNavBarBack( softwareComponents.getBackEnabled() /* mdmConfiguration.getSoftwareComponents().getNavigationBar().isBackEnabled()*/ );
////      	componenteSW.setNavBarHome( softwareComponents.getHomeEnabled() /*mdmConfiguration.getSoftwareComponents().getNavigationBar().isHomeEnabled()*/ );
////      	componenteSW.setNavBarRecent( softwareComponents.getRecentEnabled() /*mdmConfiguration.getSoftwareComponents().getNavigationBar().isRecentEnabled()*/ );
////      	componenteSW.setNotificationBar( softwareComponents.isNotificationBarEnabled() );
////      	//        componenteSW.setDesScreensaverSet(desScreensaverSet); DA SOSTITUIRE CON IL TIMEOUT
////      	//        componenteSW.setScreensaverStatus(screensaverStatus); DA INSERIRE CON enabled
////
////      	//          mdmConfiguration.getSoftwareComponents().getLanguage()
////      	//          mdmConfiguration.getSoftwareComponents().getCountry()
////      	//          mdmConfiguration.getSoftwareComponents().getLocale()
////
////      	//          mdmConfiguration.getSoftwareComponents().getTimestamp()
////      	//          mdmConfiguration.getSoftwareComponents().getTimezone()        
////      	componentiList.add(componenteSW); 
////
////      	configurazione.setComponentis(componentiList);
////
////          return configurazione;
////      };
////      mm.addConverter( configurazioneConverter3 );
////      
////      Converter<InfoFrontend, Configurazione> configurazioneConverter4 = mappingContext -> {
//////      	Failed to set value 'Configurazione(id=null, nome=null, release=null, terminalType=null, componentis=null, distribuziones=null, securities=null, applicazioneConfiguraziones=null)' on my.maven.project.configurazioni.entity.Configurazione.setApplicazioneConfiguraziones()
////      	System.out.println( mappingContext.getSource() ); //AppsFrontend
////      	System.out.println( mappingContext.getDestination() ); //null
////      	System.out.println( mappingContext.getParent()); //MappingContext[MDMConfiguration -> Configurazione]
////      	System.out.println( mappingContext.getMapping() ); //PropertyMapping[MDMConfiguration.apps -> Configurazione.applicazioneConfiguraziones]
////      	System.out.println( mappingContext.getTypeMap() ); //null
////      	InfoFrontend infoFrontend = mappingContext.getSource();
////      	
////          configurazione.setId( infoFrontend.getConfigurationId().longValue() );
////      	configurazione.setNome( infoFrontend.getConfigurationName() );
////      	configurazione.setRelease( infoFrontend.getRelease() ); // ARRIVA SOLO DA FE
////      	configurazione.setTerminalType( infoFrontend.getTerminalTypeId().longValue() );
////
////          return configurazione;
////      };
////      mm.addConverter( configurazioneConverter4 );
//      
////      Converter<String, String> configurazioneConverter = new Converter<String, String>() {
////      	public String convert(MappingContext<String, String> context) {
//////      		return (context.getSource()!= null? context.getSource().getApps().findMd5FromApk(""):"");
////      		return "";
////      	}
////      };
//
//      PropertyMap<MDMConfiguration, Configurazione> propertyMap = new PropertyMap<MDMConfiguration, Configurazione>() {
//      	   protected void configure() {
////      	      using(configurazioneConverter).map(source.getInfo().getConfigurationId()).setId(null);
////      	      using(configurazioneConverter).map(source.getInfo().getConfigurationName()).setNome(null);
////      	      using(configurazioneConverter).map(source.getInfo().getRelease()).setRelease(null);
////      	      using(configurazioneConverter).map(source.getInfo().getTerminalTypeId()).setTerminalType(null);
////      	      using(configurazioneConverter).map(source.getApps()).setApplicazioneConfiguraziones(null);
////      	      using(configurazioneConverter).map(source.getDeviceComponents()).setComponentis(null);
////      	      using(configurazioneConverter).map(source.getSoftwareComponents()).setDistribuziones(null);
////      	      using(configurazioneConverter).map(source.getSoftwareComponents()).setSecurities(null);
////      		   using(configurazioneConverter).map(source.getInfo().getConfigurationId()).setId(null);
//       	      map(source.getInfo().getConfigurationId()).setId(null);
//       	      map(source.getInfo().getConfigurationName()).setNome(null);
//       	      map(source.getInfo().getRelease()).setRelease(null);
//       	      map(source.getInfo().getTerminalTypeId()).setTerminalType(null);
//       	      map(source.getApps()).setApplicazioneConfiguraziones(null);
//       	      map(source.getDeviceComponents()).setComponentis(null);
//       	      map(source.getSoftwareComponents()).setDistribuziones(null);
//       	      map(source.getSoftwareComponents()).setSecurities(null);
//      	   }
//      	};
//      mm.addMappings(propertyMap);
////      mm.typeMap(MDMConfiguration.class, Configurazione.class).addMapping(MDMConfiguration::getInfo, Configurazione::setId);
//      mm.validate();
//      return mm;
//  }

}
