package io.github.snorrefo.event_driven_social.post.adapter.outbound.outbox

import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuartzConfiguration {

    @Bean
    fun outboxJobDetail(): JobDetail =
        JobBuilder.newJob(OutboxPublishJob::class.java)
            .withIdentity("outboxPublishJob")
            .storeDurably()
            .build()

    @Bean
    fun outboxTrigger(outboxJobDetail: JobDetail): Trigger =
        TriggerBuilder.newTrigger()
            .forJob(outboxJobDetail)
            .withIdentity("outboxPublishTrigger")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever()
            )
            .build()
}
