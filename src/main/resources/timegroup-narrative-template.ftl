<#assign groupTotalDuration = 0?number>
<#if getDescription()?has_content>${getDescription()}</#if>
<#if getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
 <#list getTimeRows() as timeRow>
  <#assign groupTotalDuration += timeRow.getDurationSecs()>
  ${(timeRow.getActivityHour() % 100)?string["00"]}:${timeRow.getFirstObservedInHour()?string["00"]} [${timeRow.getDurationSecs()
 ?string.@duration}] - ${timeRow.getActivity()} - ${timeRow.getDescription()!"N/A"}
 </#list>
</#if>
${'\n'}
<#if getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
Total worked time: ${groupTotalDuration?string.@duration}
</#if>
Total chargeable time: ${getTotalDurationSecs()?string.@duration}
Experience factor: ${getUser().getExperienceWeightingPercent()}%
