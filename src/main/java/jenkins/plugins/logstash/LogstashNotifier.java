/*
 * The MIT License
 *
 * Copyright 2014 Rusty Gerard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.logstash;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.OutputStream;
import java.io.PrintStream;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Post-build action to push build log to Logstash.
 *
 * @author Rusty Gerard
 * @since 1.0.0
 */
public class LogstashNotifier extends Notifier {

  public int maxLines;
  public boolean failBuild;

  @DataBoundConstructor
  public LogstashNotifier(int maxLines, boolean failBuild) {
    this.maxLines = maxLines;
    this.failBuild = failBuild;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
    PrintStream errorPrintStream = listener.getLogger();
    LogstashWriter logstash = getLogStashWriter(build, errorPrintStream);
    logstash.writeBuildLog(maxLines);

    return !(failBuild && logstash.isConnectionBroken());
  }

  // Method to encapsulate calls for unit-testing
  LogstashWriter getLogStashWriter(AbstractBuild<?, ?> build, OutputStream errorStream) {
    return new LogstashWriter(build, errorStream);
  }

  public BuildStepMonitor getRequiredMonitorService() {
    // We don't call Run#getPreviousBuild() so no external synchronization between builds is required
    return BuildStepMonitor.NONE;
  }

  @Override
  public Descriptor getDescriptor() {
    return (Descriptor) super.getDescriptor();
  }

  @Extension
  public static class Descriptor extends BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType) {
      return true;
    }

    public String getDisplayName() {
      return Messages.DisplayName();
    }
  }
}
