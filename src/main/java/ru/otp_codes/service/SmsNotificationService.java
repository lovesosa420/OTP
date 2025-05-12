package ru.otp_codes.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;

import java.util.Properties;

public class SmsNotificationService {

    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType;
    private String sourceAddress;
    private static Logger logger;


    public SmsNotificationService() {
        Properties config = loadConfig();
        logger = LoggerFactory.getLogger(SmsNotificationService.class);
        this.host = config.getProperty("smpp.host");
        this.port = Integer.parseInt(config.getProperty("smpp.port"));
        this.systemId = config.getProperty("smpp.system_id");
        this.password = config.getProperty("smpp.password");
        this.systemType = config.getProperty("smpp.system_type");
        this.sourceAddress = config.getProperty("smpp.source_addr");
        ;
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(SmsNotificationService.class.getClassLoader()
                    .getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            logger.error("Failed to load sms configuration");
            throw new RuntimeException("Failed to load sms configuration", e);
        }
    }

    public boolean sendCode(String destination, String code) {
        Connection connection;
        Session session;

        try {
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddress);
            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(destination);
            submitSM.setShortMessage("Your code: " + code);
            session.submit(submitSM);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send sms");
            return false;
        }
    }
}
