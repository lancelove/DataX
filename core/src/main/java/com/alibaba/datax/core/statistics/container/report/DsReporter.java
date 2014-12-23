package com.alibaba.datax.core.statistics.container.report;

import com.alibaba.datax.core.util.DataxServiceUtil;
import com.alibaba.datax.core.statistics.communication.Communication;
import com.alibaba.datax.core.statistics.communication.CommunicationTool;
import com.alibaba.datax.dataxservice.face.domain.JobStatus;
import com.alibaba.datax.dataxservice.face.domain.TaskGroupStatus;

public class DsReporter extends AbstractReporter {
    private Long jobId;

    public DsReporter(Long jobId) {
        this.jobId = jobId;
    }

    @Override
    public void reportJobCommunication(Long jobId, Communication communication) {
        JobStatus jobStatus = new JobStatus();

        jobStatus.setStage(communication.getLongCounter("stage").intValue());
        jobStatus.setTotalRecords(communication.getLongCounter("totalReadRecords"));
        jobStatus.setTotalBytes(communication.getLongCounter("totalReadBytes"));

        jobStatus.setSpeedRecords(communication.getLongCounter("recordSpeed"));
        jobStatus.setSpeedBytes(communication.getLongCounter("byteSpeed"));

        jobStatus.setErrorRecords(communication.getLongCounter("totalErrorRecords"));
        jobStatus.setErrorBytes(communication.getLongCounter("totalErrorBytes"));
        jobStatus.setPercentage(communication.getDoubleCounter("percentage"));

        jobStatus.setErrorMessage(communication.getThrowableMessage());
        DataxServiceUtil.updateJobInfo(jobId, jobStatus);
    }

    @Override
    public void reportTGCommunication(Integer taskGroupId, Communication communication) {
        TaskGroupStatus taskGroupStatus = new TaskGroupStatus();

        // 不能设置 state，否则会收到 DataXService 的报错：State should be updated be alisa ONLY.
        // taskGroupStatus.setState(communication.getState());
        taskGroupStatus.setStage(communication.getLongCounter("stage").intValue());
        taskGroupStatus.setTotalRecords(CommunicationTool.getTotalReadRecords(communication));
        taskGroupStatus.setTotalBytes(CommunicationTool.getTotalReadBytes(communication));

        taskGroupStatus.setSpeedRecords(communication.getLongCounter(CommunicationTool.RECORD_SPEED));
        taskGroupStatus.setSpeedBytes(communication.getLongCounter(CommunicationTool.BYTE_SPEED));

        taskGroupStatus.setErrorRecords(CommunicationTool.getTotalErrorRecords(communication));
        taskGroupStatus.setErrorBytes(CommunicationTool.getTotalErrorBytes(communication));

        taskGroupStatus.setErrorMessage(communication.getThrowableMessage());

        DataxServiceUtil.updateTaskGroupInfo(this.jobId, taskGroupId, taskGroupStatus);
    }

}
