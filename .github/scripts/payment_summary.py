"""Build an HTML pass/fail summary per payment method from TestNG's testng-results.xml.

Usage: python payment_summary.py <testng-results.xml>
Prints an HTML fragment for the scheduled-run email. The payment method is the 3rd
parameter of CancelOrderTest.Steps (testCaseId, products, paymentMethod, cancelOrder).
Retried attempts (retried="true") are ignored, so each case counts once, by its final result.
"""
import html
import sys
import xml.etree.ElementTree as ET
from collections import OrderedDict

PAYMENT_PARAM = 2


def param(method, index):
    for p in method.iter("param"):
        if p.get("index") == str(index):
            value = p.find("value")
            return (value.text or "").strip() if value is not None else ""
    return ""


def main(path):
    try:
        root = ET.parse(path).getroot()
    except (OSError, ET.ParseError):
        print("<p><i>Payment method summary unavailable - no TestNG results were produced.</i></p>")
        return

    stats = OrderedDict()
    failures = []
    for m in root.iter("test-method"):
        if m.get("is-config") == "true" or m.get("retried") == "true":
            continue
        method = param(m, PAYMENT_PARAM) or "(unknown)"
        status = m.get("status", "")
        s = stats.setdefault(method, {"PASS": 0, "FAIL": 0, "SKIP": 0})
        s[status] = s.get(status, 0) + 1
        if status == "FAIL":
            exc = m.find("exception")
            msg = ""
            if exc is not None:
                msg = (exc.findtext("message") or "").strip() or exc.get("class", "")
            msg = msg.splitlines()[0][:200] if msg else "-"
            failures.append((param(m, 0), param(m, 1), method, msg))

    if not stats:
        print("<p><i>Payment method summary unavailable - no test cases ran.</i></p>")
        return

    cell = 'style="text-align:center"'
    out = ['<h3>Result by Payment Method</h3>',
           '<table border="1" cellpadding="6" cellspacing="0">',
           '<tr style="background:#eee"><th>Payment Method</th><th>Total</th>'
           '<th>Passed</th><th>Failed</th><th>Skipped</th></tr>']
    totals = {"PASS": 0, "FAIL": 0, "SKIP": 0}
    for method, s in stats.items():
        total = s["PASS"] + s["FAIL"] + s["SKIP"]
        for k in totals:
            totals[k] += s[k]
        fail_style = ' style="text-align:center;color:#c00;font-weight:bold"' if s["FAIL"] else f" {cell}"
        out.append(f'<tr><td>{html.escape(method)}</td><td {cell}>{total}</td>'
                   f'<td {cell}>{s["PASS"]}</td><td{fail_style}>{s["FAIL"]}</td>'
                   f'<td {cell}>{s["SKIP"]}</td></tr>')
    grand = sum(totals.values())
    out.append(f'<tr style="font-weight:bold"><td>Total</td><td {cell}>{grand}</td>'
               f'<td {cell}>{totals["PASS"]}</td><td {cell}>{totals["FAIL"]}</td>'
               f'<td {cell}>{totals["SKIP"]}</td></tr></table>')

    if failures:
        out += ['<h3>Failed Cases</h3>',
                '<table border="1" cellpadding="6" cellspacing="0">',
                '<tr style="background:#eee"><th>Test Case</th><th>Products</th>'
                '<th>Payment Method</th><th>Reason</th></tr>']
        for tc, products, method, msg in failures:
            out.append(f'<tr><td>{html.escape(tc)}</td><td>{html.escape(products)}</td>'
                       f'<td>{html.escape(method)}</td><td>{html.escape(msg)}</td></tr>')
        out.append('</table>')

    print("\n".join(out))


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "target/surefire-reports/testng-results.xml")
