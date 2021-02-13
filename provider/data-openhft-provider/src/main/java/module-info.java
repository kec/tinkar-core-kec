/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.hl7.tinkar.common.provider.chronology.openhft.OpenHFTProvider;
import org.hl7.tinkar.common.service.PrimitiveDataService;

@SuppressWarnings("module") // 7 in HL7 is not a version reference
module org.hl7.tinkar.provider.chronology.openhft {
    requires java.base;
    requires org.eclipse.collections.api;
    requires org.hl7.tinkar.component;
    requires org.hl7.tinkar.common;
    requires com.google.auto.service;
    requires chronicle.map;
    requires chronicle.core;
    requires chronicle.values;
    requires io.activej.bytebuf;
    requires org.hl7.tinkar.dto;
    requires org.apache.logging.log4j;
    requires org.hl7.tinkar.entity;
    requires chronicle.bytes;
    requires jdk.unsupported;
    provides PrimitiveDataService
            with OpenHFTProvider;
}
