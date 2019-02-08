<#assign groupTotalDuration = 0?number>
<#if getDescription()?has_content>${getDescription()}</#if>
<#if getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
 <#list getTimeRows() as timeRow>
  <#assign groupTotalDuration += timeRow.getDurationSecs()>
  ${timeRow.getActivityHour()}:${timeRow.getFirstObservedInHour()} [${timeRow.getDurationSecs() / 60} mins] -
  ${timeRow.getActivity()} - ${timeRow.getDescription()!"N/A"}
 </#list>
</#if>
<#if getDescription()?has_content || getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
 ${'\n'}
Total worked time: ${groupTotalDuration?string.@duration}
Total chargeable time: ${getTotalDurationSecs()?string.@duration}
Experience factor: ${getUser().getExperienceWeightingPercent()}%
</#if>
