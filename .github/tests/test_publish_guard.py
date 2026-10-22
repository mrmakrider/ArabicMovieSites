"""Exercise the workflow's actual publishing commands with git mocked; never publish."""
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

WORKFLOW = Path(__file__).resolve().parents[1] / "workflows" / "build.yml"
SHA = "a" * 40
NEWER_SHA = "b" * 40


class PublishGuardTest(unittest.TestCase):
    def run_publish(self, tip=SHA, remote_exit="0", has_changes="1"):
        source = WORKFLOW.read_text()
        block = source.split("      - name: Commit and push builds\n", 1)[1]
        script = block.split("        run: |\n", 1)[1]
        script = "\n".join(line[10:] for line in script.splitlines())
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            git = root / "git"
            git.write_text('''#!/bin/bash
printf '%s\\n' "$*" >> "$CALL_LOG"
case "$1" in
  ls-remote)
    if [[ "$REMOTE_EXIT" != 0 ]]; then exit "$REMOTE_EXIT"; fi
    if [[ -n "$TIP" ]]; then printf '%s\\t%s\\n' "$TIP" "$GITHUB_REF"; fi
    ;;
  diff) exit "$HAS_CHANGES" ;;
esac
''')
            git.chmod(0o755)
            log = root / "calls"
            env = dict(os.environ, PATH=str(root) + os.pathsep + os.environ["PATH"],
                       CALL_LOG=str(log), TIP=tip, REMOTE_EXIT=remote_exit,
                       HAS_CHANGES=has_changes, GITHUB_SHA=SHA,
                       GITHUB_REF="refs/heads/master")
            result = subprocess.run(["bash", "-c", script], env=env,
                                    capture_output=True, text=True, cwd=root)
            return result, log.read_text().splitlines()

    def test_current_tip_publishes_after_freshness_check(self):
        result, calls = self.run_publish()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual("push origin HEAD:builds", calls[-1])
        self.assertEqual("ls-remote --exit-code origin refs/heads/master", calls[-2])

    def test_older_validation_finishing_last_cannot_overwrite_newer_build(self):
        result, calls = self.run_publish(tip=NEWER_SHA)
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertIn("Skipping superseded build", result.stdout)
        self.assertFalse(any(call.startswith("push ") for call in calls))

    def test_remote_lookup_failure_blocks_publication(self):
        result, calls = self.run_publish(remote_exit="2")
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(any(call.startswith("push ") for call in calls))

    def test_empty_tip_blocks_publication(self):
        result, calls = self.run_publish(tip="")
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(any(call.startswith("push ") for call in calls))

    def test_malformed_tip_blocks_publication(self):
        result, calls = self.run_publish(tip="not-a-sha")
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(any(call.startswith("push ") for call in calls))

    def test_no_changes_neither_commits_nor_pushes(self):
        result, calls = self.run_publish(has_changes="0")
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertFalse(any(call.startswith(("commit ", "push ")) for call in calls))


if __name__ == "__main__":
    unittest.main()
