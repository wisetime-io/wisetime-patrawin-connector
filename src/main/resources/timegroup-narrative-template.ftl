<#if getDescription()?has_content>${getDescription()}</#if>
<#if getNarrativeType() == "NARRATIVE_AND_TIME_ROW_ACTIVITY_DESCRIPTIONS">
 ${'\n'}
 <#list getTimeRows() as timeRow>
  ${(timeRow.getActivityHour() % 100)?string["00"]}:${timeRow.getFirstObservedInHour()?string["00"]} [${timeRow.getDurationSecs()
 ?string.@duration}] - ${timeRow.getActivity()} - ${timeRow.getDescription()!"N/A"}
 </#list>
</#if>
${'\n'}
Total worked time: ${getTotalDuration(getTimeRows())?string.@duration}
Total chargeable time: ${getTotalDurationSecs()?string.@duration}
Experience factor: ${getUser().getExperienceWeightingPercent()}%
<#if getDurationSplitStrategy() == "DIVIDE_BETWEEN_TAGS">
${'\n'}
The above times have been split across ${getTimeRows()?size} cases and are thus greater than the chargeable time on this
 case.
</#if>

<#function getTotalDuration timeRows>
 <#local rowTotalDuration = 0?number>
 <#list timeRows as timeRow>
  <#local rowTotalDuration += timeRow.getDurationSecs()>
 </#list>
 <#return rowTotalDuration />
</#function>
