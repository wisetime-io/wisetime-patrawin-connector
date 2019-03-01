<#if getDescription()?has_content>${getDescription()}</#if>
<#if getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
<#list getSegmentHourBlocks(getTimeRows())?sort as segmentHourBlock>
${'\r\n'}${segmentHourBlock}:00 - ${segmentHourBlock}:59
 <#list getTimeRows() as timeRow>
  <#assign timeRowSegmentHour = (timeRow.getActivityHour() % 100)?string["00"]>
  <#if segmentHourBlock == timeRowSegmentHour>
- ${timeRow.getDurationSecs()?string.@duration} - ${sanitizeAppName(timeRow.getActivity())} - ${sanitizeWindowTitle(timeRow.getDescription()!"@_empty_@")}
  </#if>
 </#list>
</#list>
</#if>
${'\r\n'}Total Worked Time: ${getTotalDuration(getTimeRows())?string.@duration}
Total Chargeable Time: ${getTotalDurationSecs()?string.@duration}
Experience Weighting: ${getUser().getExperienceWeightingPercent()}%
<#if getDurationSplitStrategy() == "DIVIDE_BETWEEN_TAGS" && (getTags()?size > 1)>
${'\r\n'}The above times have been split across ${getTags()?size} items and are thus greater than the chargeable time in this item
</#if>

<#function getTotalDuration timeRows>
 <#local rowTotalDuration = 0?number>
 <#list timeRows as timeRow>
  <#local rowTotalDuration += timeRow.getDurationSecs()>
 </#list>
 <#return rowTotalDuration />
</#function>

<#function getSegmentHourBlocks timeRows>
 <#local segmentHours = []>
 <#list timeRows as timeRow>
  <#local segmentHour = (timeRow.getActivityHour() % 100)?string["00"]>
  <#if !(segmentHours?seq_contains(segmentHour))>
   <#local segmentHours += [segmentHour]>
  </#if>
 </#list>
 <#return segmentHours />
</#function>

<#function sanitizeAppName appName>
 <#local newAppName = appName>
 <#if appName?starts_with("@_") && appName?ends_with("_@")>
  <#local newAppName = appName?substring(2, (appName?length - 2))>
 </#if>
 <#return newAppName />
</#function>

<#function sanitizeWindowTitle winTitle>
 <#if winTitle == "@_empty_@" >
  <#return "No window title available" />
 </#if>
 <#return winTitle />
</#function>