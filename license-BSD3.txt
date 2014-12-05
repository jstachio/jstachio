<#if licenseFirst??>
${licenseFirst}
</#if>
${licensePrefix}Copyright (c) ${date?date?string("yyyy")}, ${project.organization!user}
${licensePrefix}All rights reserved.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}Redistribution and use in source and binary forms, with or without modification,
${licensePrefix}are permitted provided that the following conditions are met:
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix} 1. Redistributions of source code must retain the above copyright notice,
${licensePrefix}    this list of conditions and the following disclaimer.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix} 2. Redistributions in binary form must reproduce the above copyright notice,
${licensePrefix}    this list of conditions and the following disclaimer in the documentation and/or
${licensePrefix}    other materials provided with the distribution.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix} 3. Neither the name of the copyright holder nor the names of its contributors
${licensePrefix}    may be used to endorse or promote products derived from this software
${licensePrefix}    without specific prior written permission.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix} THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
${licensePrefix} ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
${licensePrefix} THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
${licensePrefix} IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
${licensePrefix} ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
${licensePrefix} (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
${licensePrefix}  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
${licensePrefix} ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
${licensePrefix} (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
${licensePrefix} EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
<#if licenseLast??>
${licenseLast}
</#if>